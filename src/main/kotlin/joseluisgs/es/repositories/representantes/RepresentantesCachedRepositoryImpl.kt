package joseluisgs.es.repositories.representantes

import joseluisgs.es.exceptions.DataBaseIntegrityViolationException
import joseluisgs.es.models.Representante
import joseluisgs.es.services.cache.representantes.RepresentantesCache
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
@Named("RepresentantesCachedRepository")
/**
 * Repositorio de [Representante] con caché
 * @param repository Repositorio de datos originales
 * @param cache Desacoplamos la cache
 * @property refreshJob Job para cancelar la ejecución
 * @constructor Crea un repositorio de Representantes con caché
 * @see RepresentantesRepository
 * @see RepresentantesCache
 * @see Representante
 */
class RepresentantesCachedRepositoryImpl(
    @Named("PersonasRepository") // Repositorio de datos originales
    private val repository: RepresentantesRepository,
    private val cache: RepresentantesCache // Desacoplamos la cache
) : RepresentantesRepository {

    private var refreshJob: Job? = null // Job para cancelar la ejecución

    /**
     * Inicializamos el repositorio
     * Si tenemos un proceso de refresco de datos, lo iniciamos
     * @see refreshCacheJob
     * @see RepresentantesCache.hasRefreshAllCacheJob
     */
    init {
        logger.debug { "Inicializando el repositorio cache representantes. AutoRefreshAll: ${cache.hasRefreshAllCacheJob}" }
        // Iniciamos el proceso de refresco de datos
        // No es obligatorio hacerlo, pero si queremos que se refresque
        if (cache.hasRefreshAllCacheJob)
            refreshCacheJob()
    }

    /**
     * Refrescamos el cache, con un job en segundo plano
     * @see refreshJob
     * @see RepresentantesCache.refreshTime
     */
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

    /**
     * Buscamos todos los representantes en cache
     * Si no tenemos datos en cache, los buscamos en el repositorio
     * @return Flow de Representantes
     */
    override suspend fun findAll(): Flow<Representante> {
        logger.debug { "findAll: Buscando todos los representantes en cache" }

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

    /**
     * Buscamos todos los representantes en cache paginados
     * Si no tenemos datos en cache, los buscamos en el repositorio
     * @param page Página
     * @param perPage Cantidad por página
     * @return Flow de Representantes
     * @see findAll
     */
    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Representante> {
        logger.debug { "findAllPageable: Buscando todos los representantes con página: $page y cantidad: $perPage" }

        // Aquí no se puede cachear, ya que no se puede saber si hay más páginas
        // idem al findAll
        return repository.findAllPageable(page, perPage)
    }

    /**
     * Buscamos todos los representantes en cache por nombre
     * Si no tenemos datos en cache, los buscamos en el repositorio
     * @param nombre Nombre del representante
     * @return Flow de Representantes
     */
    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findByNombre: Buscando representante con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    /**
     * Buscamos todos los representantes por su id
     * Si no tenemos datos en cache, los buscamos en el repositorio
     * @param id Id del representante
     * @return Representante? null si no existe
     * @see RepresentantesRepositoryImpl.findById
     */
    override suspend fun findById(id: UUID): Representante? {
        logger.debug { "findById: Buscando representante en cache con id: $id" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.cache.get(id) ?: repository.findById(id)
            ?.also { cache.cache.put(id, it) }
    }

    /**
     * Guardamos un representante en cache y en el repositorio
     * @param entity Representante a guardar
     * @return Representante guardado
     * @see RepresentantesRepositoryImpl.save
     */
    override suspend fun save(entity: Representante): Representante {
        logger.debug { "save: Guardando representante en cache" }

        // Guardamos en el repositorio y en la cache en paralelo, creando antes el id
        val representante =
            entity.copy(id = UUID.randomUUID(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        // Creamos scope
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            cache.cache.put(representante.id, representante)
        }
        scope.launch {
            repository.save(representante)
        }
        return representante
    }

    /**
     * Actualizamos un representante en cache y en el repositorio
     * @param id Id del representante a actualizar
     * @param entity Representante a actualizar
     * @return Representante? actualizado o null si no existe
     * @see RepresentantesRepositoryImpl.update
     */
    override suspend fun update(id: UUID, entity: Representante): Representante? {
        logger.debug { "update: Actualizando representante en cache" }

        // Debemos ver si existe en la cache, pero...
        // si no existe puede que esté en el repositorio, pero no en la cache
        // todo depende de si hemos limitado el tamaño de la cache y su tiempo de vida
        // o si nos hemos traído todos los datos en el findAll
        val existe = findById(id) // hace todo lo anterior
        return existe?.let {
            // Actualizamos en el repositorio y en la cache en paralelo creando antes el id, tomamos el created de quien ya estaba
            val representante = entity.copy(id = id, createdAt = existe.createdAt, updatedAt = LocalDateTime.now())
            // Creamos scope
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                cache.cache.put(representante.id, representante)
            }
            scope.launch {
                repository.update(id, representante)
            }
            return representante
        }
    }

    /**
     * Eliminamos un representante en cache y en el repositorio
     * @param entity Representante a eliminar
     * @return Representante? eliminado o null si no existe
     * @see RepresentantesRepositoryImpl.delete
     */
    override suspend fun delete(entity: Representante): Representante? {
        logger.debug { "delete: Eliminando representante en cache" }

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