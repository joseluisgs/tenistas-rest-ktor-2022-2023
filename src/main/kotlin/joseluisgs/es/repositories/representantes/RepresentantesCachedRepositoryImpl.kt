package joseluisgs.es.repositories.representantes

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.exceptions.RepresentanteException
import joseluisgs.es.models.Representante
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import mu.KotlinLogging
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

class RepresentantesCachedRepositoryImpl(
    private val repository: RepresentantesRepository, // Repositorio de datos originales
) : RepresentantesRepository {
    @OptIn(ExperimentalTime::class)
    private val cache = Cache.Builder()
        .expireAfterAccess(24.hours) // Vamos a cachear durante 1 hora
        .build<UUID, Representante>()

    private val refreshTime = 60 * 60 * 1000L // 1 hora en milisegundos


    init {
        logger.debug { "Iniciando Repositorio de cache de Representantes" }
        // aunque esto no es muy recomendable, al iniciarme cargo el cache
        // Como mejora se podría hacer con una corrutina con un temprizador
        // Pero cargando la cache al inicio, luego "irá" viviendo con los datos que se vayan
        // solicitando y actualizando
        runBlocking {
            refreshCache()
        }

    }

    private suspend fun refreshCache() = withContext(Dispatchers.IO) {
        logger.debug { "refreshCache: Refrescando cache de Representantes" }
        launch {
            do {
                // Invalidamos la cache: podiramos no es necesario, pero lo hacemos para que se vea
                repository.findAll().collect {
                    cache.put(it.id, it) // añadimos a la cache, si ya existe lo actualiza
                }
                delay(refreshTime)
            } while (true)
        }
    }

    override fun findAll(): Flow<Representante> {
        logger.debug { "findAll: Buscando todos los representantes en cache" }

        // Devolvemos la cache
        return cache.asMap().values.asFlow()
    }


    override fun findAllPageable(page: Int, perPage: Int): Flow<Representante> {
        logger.debug { "findAllPageable: Buscando todos los representantes en cache con página: $page y cantidad: $perPage" }

        // Aquí no se puede cachear, ya que no se puede saber si hay más páginas
        return repository.findAllPageable(page, perPage)
    }


    override suspend fun findById(id: UUID): Representante {
        logger.debug { "findById: Buscando representante con id: $id en cache" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.get(id) {
            repository.findById(id) ?: throw RepresentanteException("No se ha encontrado el representante con id: $id")
        }
    }

    override fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findByNombre: Buscando representante en cache con nombre: $nombre" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.asMap().values.filter { it.nombre == nombre }.asFlow()
    }

    override suspend fun save(entity: Representante): Representante {
        logger.debug { "save: Guardando representante en cache" }

        // Guardamos en el repositorio
        val representante = repository.save(entity)
        // Añadimos a la cache
        cache.put(representante.id, representante)
        return representante
    }

    override suspend fun update(id: UUID, entity: Representante): Representante {
        logger.debug { "update: Actualizando representante en cache" }

        // Actualizamos en el repositorio
        val representante = repository.update(id, entity)
        // Actualizamos en la cache
        cache.put(representante.id, representante)
        return representante
    }

    override suspend fun delete(id: UUID): Representante {
        logger.debug { "delete: Eliminando representante en cache" }

        // Eliminamos en el repositorio
        val representante = repository.delete(id)
        // Eliminamos en la cache
        cache.invalidate(id)
        return representante
    }
}