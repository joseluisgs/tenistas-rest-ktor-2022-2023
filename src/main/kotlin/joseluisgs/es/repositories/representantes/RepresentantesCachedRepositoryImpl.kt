package joseluisgs.es.repositories.representantes

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.models.Representante
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import mu.KotlinLogging
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}


class RepresentantesCachedRepositoryImpl(
    private val repository: RepresentantesRepository, // Repositorio de datos originales
    private val refreshJob: Job? = null // Job para cancelar la ejecución
) : RepresentantesRepository {
    @OptIn(ExperimentalTime::class)
    private val cache = Cache.Builder()
        .expireAfterAccess(24.hours) // Vamos a cachear durante 1 hora
        .build<UUID, Representante>()

    private val refreshTime = 60 * 60 * 1000L // 1 hora en milisegundos

    init {
        logger.debug { "Inicializando el repositorio cache representantes" }
        // Iniciamos el proceso de refresco de datos
        refreshCache()
    }

    private fun refreshCache() {
        // Background job para refrescar el cache
        // Si tenemos muchos datos, solo se mete en el cache los que se van a usar:
        // create, findById, update, delete
        // Creamos un Scope propio para que no se enlazado con el actual.
        CoroutineScope(Dispatchers.IO).launch {
            refreshJob?.cancel() // Cancelamos el job si existe
            do {
                logger.debug { "refreshCache: Refrescando cache de Representantes" }
                repository.findAll().collect { representantes ->
                    representantes.forEach { representante ->
                        cache.put(representante.id, representante)
                    }
                }
                logger.debug { "refreshCache: Cache actualizada: ${cache.asMap().values.size}" }
                delay(refreshTime)
            } while (true)
        }
    }

    override suspend fun findAll(): Flow<List<Representante>> {
        logger.debug { "findAll: Buscando todos los representantes en cache" }

        // Si por alguna razón no tenemos datos en el cache, los buscamos en el repositorio
        return if (cache.asMap().values.isEmpty()) {
            // refreshCache()
            logger.debug { "findAll: Cache vacía, buscando en base de datos" }
            repository.findAll()
        } else {
            logger.debug { "findAll: Cache con datos, devolviendo datos de cache" }
            flowOf(cache.asMap().values.toList())
        }

    }


    override fun findAllPageable(page: Int, perPage: Int): Flow<List<Representante>> {
        logger.debug { "findAllPageable: Buscando todos los representantes en cache con página: $page y cantidad: $perPage" }

        // Aquí no se puede cachear, ya que no se puede saber si hay más páginas
        return repository.findAllPageable(page, perPage)
    }

    override fun findByNombre(nombre: String): Flow<List<Representante>> {
        logger.debug { "findByNombre: Buscando representante en cache con nombre: $nombre" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return flow { cache.asMap().values.filter { it.nombre == nombre } }
    }


    override suspend fun findById(id: UUID): Representante? {
        logger.debug { "findById: Buscando representante con id: $id en cache" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.get(id) ?: repository.findById(id)?.also { cache.put(id, it) }
    }


    override suspend fun save(entity: Representante): Representante {
        logger.debug { "save: Guardando representante en cache" }

        // Guardamos en el repositorio
        val representante = repository.save(entity)
        // Añadimos a la cache
        cache.put(representante.id, representante)
        return representante
    }

    override suspend fun update(id: UUID, entity: Representante): Representante? {
        logger.debug { "update: Actualizando representante en cache" }

        // Actualizamos en el repositorio
        val representante = repository.update(id, entity)
        // Actualizamos en la cache
        return representante?.also { cache.put(id, it) }
    }

    override suspend fun delete(id: UUID): Representante? {
        logger.debug { "delete: Eliminando representante en cache" }

        // Eliminamos en el repositorio
        val representante = repository.delete(id)
        // Eliminamos en la cache
        return representante?.also { cache.invalidate(id) }
    }
}