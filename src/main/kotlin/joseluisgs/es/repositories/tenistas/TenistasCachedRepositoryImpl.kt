package joseluisgs.es.repositories.tenistas

import joseluisgs.es.models.Tenista
import joseluisgs.es.services.cache.tenistas.TenistasCache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
@Named("TenistasCachedRepository")
class TenistasCachedRepositoryImpl(
    @Named("TenistasRepository") // Repositorio de datos originales
    private val repository: TenistasRepository,
    private val cache: TenistasCache // Desacoplamos la cache
) : TenistasRepository {

    private var refreshJob: Job? = null // Job para cancelar la ejecución


    init {
        logger.debug { "Inicializando el repositorio cache tenistas. AutoRefreshAll: ${cache.hasRefreshAllCacheJob}" }
        // Iniciamos el proceso de refresco de datos
        // No es obligatorio hacerlo, pero si queremos que se refresque
        if (cache.hasRefreshAllCacheJob)
            refreshCacheJob()
    }

    private fun refreshCacheJob() {
        // Background job para refrescar el cache
        // Si tenemos muchos datos, solo se mete en el cache los que se van a usar:
        // create, findById, update, delete
        // Creamos un Scope propio para que no se enlazado con el actual.
        if (refreshJob != null)
            refreshJob?.cancel()

        refreshJob = CoroutineScope(Dispatchers.IO).launch {
            // refreshJob?.cancel() // Cancelamos el job si existe
            do {
                logger.debug { "refreshCache: Refrescando cache de Representantes" }
                repository.findAll().collect { representante ->
                    cache.cache.put(representante.id, representante)
                }
                logger.debug { "refreshCache: Cache actualizada: ${cache.cache.asMap().values.size}" }
                delay(cache.refreshTime)
            } while (true)
        }
    }

    override suspend fun findAll(): Flow<Tenista> {
        logger.debug { "findAll: Buscando todos los tenistas en cache" }

        // Si por alguna razón no tenemos datos en el cache, los buscamos en el repositorio
        // Ojo si le hemos puesto tamaño máximo a la caché, puede que no estén todos los datos
        // si no en los findAll, siempre devolver los datos del repositorio y no hacer refresco

        return if (!cache.hasRefreshAllCacheJob || cache.cache.asMap().isEmpty()) {
            logger.debug { "findAll: Devolviendo datos de repositorio" }
            repository.findAll()
        } else {
            logger.debug { "findAll: Devolviendo datos de cache" }
            cache.cache.asMap().values.asFlow()
        }
    }


    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Tenista> {
        logger.debug { "findAllPageable: Buscando todos los tenistas con página: $page y cantidad: $perPage" }

        // Aquí no se puede cachear, ya que no se puede saber si hay más páginas
        // idem al findAll
        return repository.findAllPageable(page, perPage)
    }

    override suspend fun findByRanking(ranking: Int): Tenista? {
        logger.debug { "findByRanking: Buscando tenista con ranking: $ranking" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.cache.asMap().values.find { it.ranking == ranking }
            ?: repository.findByRanking(ranking)
                ?.also { cache.cache.put(it.id, it) }
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> {
        logger.debug { "findByNombre: Buscando tenista con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    override suspend fun findById(id: UUID): Tenista? {
        logger.debug { "findById: Buscando tenista en cache con id: $id" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.cache.get(id) ?: repository.findById(id)
            ?.also { cache.cache.put(id, it) }
    }


    override suspend fun save(entity: Tenista): Tenista {
        logger.debug { "save: Guardando tenista en cache" }

        // Guardamos en el repositorio y en la cache en paralelo, creando antes el id
        val tenista =
            entity.copy(id = UUID.randomUUID(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        // Creamos scope
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            cache.cache.put(tenista.id, tenista)
        }
        scope.launch {
            repository.save(tenista)
        }
        return tenista
    }

    override suspend fun update(id: UUID, entity: Tenista): Tenista? {
        logger.debug { "update: Actualizando tenista en cache" }

        // Debemos ver si existe en la cache, pero...
        // si no existe puede que esté en el repositorio, pero no en la cache
        // todo depende de si hemos limitado el tamaño de la cache y su tiempo de vida
        // o si nos hemos traído todos los datos en el findAll
        val existe = findById(id) // hace todo lo anterior
        return existe?.let {
            // Actualizamos en el repositorio y en la cache en paralelo creando antes el id, tomamos el created de quien ya estaba
            val tenista = entity.copy(id = id, createdAt = existe.createdAt, updatedAt = LocalDateTime.now())
            // Creamos scope
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                cache.cache.put(tenista.id, tenista)
            }
            scope.launch {
                repository.update(id, tenista)
            }
            return tenista
        }
    }

    override suspend fun delete(entity: Tenista): Tenista? {
        logger.debug { "delete: Eliminando tenista en cache" }

        // existe?
        val existe = findById(entity.id)
        return existe?.let {
            // Eliminamos en el repositorio y en la cache en paralelo
            // Creamos scope y un handler para el error
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                cache.cache.invalidate(entity.id)
            }
            scope.launch {
                repository.delete(entity)
            }

            return existe
        }
    }
}