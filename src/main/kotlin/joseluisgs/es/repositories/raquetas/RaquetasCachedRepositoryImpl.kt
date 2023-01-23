package joseluisgs.es.repositories.raquetas

import joseluisgs.es.exceptions.DataBaseIntegrityViolationException
import joseluisgs.es.models.Raqueta
import joseluisgs.es.services.cache.raquetas.RaquetasCache
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
@Named("RaquetasCachedRepository")
class RaquetasCachedRepositoryImpl(
    @Named("RaquetasRepository") // Repositorio de datos originales
    private val repository: RaquetasRepository,
    private val cache: RaquetasCache // Desacoplamos la cache
) : RaquetasRepository {

    private var refreshJob: Job? = null // Job para cancelar la ejecución


    init {
        logger.debug { "Inicializando el repositorio cache raquetas. AutoRefreshAll: ${cache.hasRefreshAllCacheJob}" }
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
                logger.debug { "refreshCache: Refrescando cache de Raquetas" }
                repository.findAll().collect { representante ->
                    cache.cache.put(representante.id, representante)
                }
                logger.debug { "refreshCache: Cache actualizada: ${cache.cache.asMap().values.size}" }
                delay(cache.refreshTime)
            } while (true)
        }
    }

    override suspend fun findAll(): Flow<Raqueta> {
        logger.debug { "findAll: Buscando todos las raquetas en cache" }

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


    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta> {
        logger.debug { "findAllPageable: Buscando todos las raquetas en cache con página: $page y cantidad: $perPage" }

        // Aquí no se puede cachear, ya que no se puede saber si hay más páginas
        // idem al findAll
        return repository.findAllPageable(page, perPage)
    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "findByNombre: Buscando raquetas en cache con marca: $marca" }

        return repository.findByMarca(marca)
    }

    override suspend fun findById(id: UUID): Raqueta? {
        logger.debug { "findById: Buscando raqueta en cache con id: $id" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.cache.get(id) ?: repository.findById(id)
            ?.also { cache.cache.put(id, it) }
    }

    override suspend fun save(entity: Raqueta): Raqueta {
        logger.debug { "save: Guardando raqueta en cache" }

        // Guardamos en el repositorio y en la cache en paralelo, creando antes el id
        val raqueta =
            entity.copy(id = UUID.randomUUID(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        // Creamos scope
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            cache.cache.put(raqueta.id, raqueta)
        }
        scope.launch {
            repository.save(raqueta)
        }
        return raqueta
    }

    override suspend fun update(id: UUID, entity: Raqueta): Raqueta? {
        logger.debug { "update: Actualizando raqueta en cache" }

        // Debemos ver si existe en la cache, pero...
        // si no existe puede que esté en el repositorio, pero no en la cache
        // todo depende de si hemos limitado el tamaño de la cache y su tiempo de vida
        // o si nos hemos traído todos los datos en el findAll
        val existe = findById(id) // hace todo lo anterior
        return existe?.let {
            // Actualizamos en el repositorio y en la cache en paralelo creando antes el id, tomamos el created de quien ya estaba
            val raqueta = entity.copy(id = id, createdAt = existe.createdAt, updatedAt = LocalDateTime.now())
            // Creamos scope
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                cache.cache.put(raqueta.id, raqueta)
            }
            scope.launch {
                repository.update(id, raqueta)
            }
            return raqueta
        }
    }

    override suspend fun delete(entity: Raqueta): Raqueta? {
        logger.debug { "delete: Eliminando raqueta en cache" }

        // existe?
        val existe = findById(entity.id)
        return existe?.let {

            // Eliminamos en el repositorio y en la cache en paralelo
            // Creamos scope y un handler para el error
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                cache.cache.invalidate(entity.id)
            }

            val delete = scope.async {
                repository.delete(entity)
            }
            // igual que try catch
            runCatching {
                delete.await()
            }.onFailure {
                throw DataBaseIntegrityViolationException()
            }

            return existe
        }
    }
}