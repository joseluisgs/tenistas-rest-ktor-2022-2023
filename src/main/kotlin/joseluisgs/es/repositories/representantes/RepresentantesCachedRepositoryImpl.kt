package joseluisgs.es.repositories.representantes

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.models.Representante
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}


class RepresentantesCachedRepositoryImpl(
    private val repository: RepresentantesRepository, // Repositorio de datos originales
    private val refreshJob: Job? = null // Job para cancelar la ejecución
) : RepresentantesRepository {
    @OptIn(ExperimentalTime::class)
    private val cache = Cache.Builder()
        // Si le ponemos opciones de cacheo si no usara las de por defecto
        .maximumCacheSize(100) // Tamaño máximo de la caché si queremos limitarla
        .expireAfterAccess(30.minutes) // Vamos a cachear durante
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
        // Ojo si le hemos puesto tamaño máximo a la caché, puede que no estén todos los datos
        // si no en los findAll, siempre devolver los datos del repositorio

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
        // idem al findAll
        return repository.findAllPageable(page, perPage)
    }

    override fun findByNombre(nombre: String): Flow<List<Representante>> {
        logger.debug { "findByNombre: Buscando representante en cache con nombre: $nombre" }

        // Buscamos en la cache
        return flowOf(
            cache.asMap().values.filter {
                it.nombre.lowercase(Locale.getDefault()).contains(nombre.lowercase(Locale.getDefault()))
            }
        )
    }


    override suspend fun findById(id: UUID): Representante? {
        logger.debug { "findById: Buscando representante en cache con id: $id" }

        // Buscamos en la cache y si no está, lo buscamos en el repositorio y lo añadimos a la cache
        return cache.get(id) ?: repository.findById(id)?.also { cache.put(id, it) }
    }


    override suspend fun save(entity: Representante): Representante {
        logger.debug { "save: Guardando representante en cache" }

        // Guardamos en el repositorio y en la cache en paralelo, creando antes el id
        val representante =
            entity.copy(id = UUID.randomUUID(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        // Creamos scope
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repository.save(representante)
        }
        scope.launch {
            cache.put(representante.id, representante)
        }
        return representante
    }

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
                repository.update(id, representante)
            }
            scope.launch {
                cache.put(representante.id, representante)
            }
            return representante
        }
    }

    override suspend fun delete(id: UUID): Representante? {
        logger.debug { "delete: Eliminando representante en cache" }

        // existe?
        val existe = findById(id)
        return existe?.let {
            // Eliminamos en el repositorio y en la cache en paralelo
            // Creamos scope
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                repository.delete(id)
            }
            scope.launch {
                cache.invalidate(id)
            }
            return existe
        }
    }
}