package joseluisgs.es.repositories.representantes

import joseluisgs.es.db.getRepresentantesInit
import joseluisgs.es.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

class RepresentantesRepositoryImpl : RepresentantesRepository {

    // Fuente de datos
    private val representantes: MutableMap<UUID, Representante> = mutableMapOf()

    init {
        logger.debug { "Iniciando Repositorio de Representantes" }

        getRepresentantesInit().forEach {
            representantes[it.id] = it
        }
    }

    override suspend fun findAll(): Flow<List<Representante>> {
        logger.debug { "findAll: Buscando todos los representantes" }

        // Filtramos por página y por perPage
        return flowOf(representantes.values.toList())
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<List<Representante>> {
        logger.debug { "findAllPageable: Buscando todos los representantes con página: $page y cantidad: $perPage" }

        // Filtramos por página y por perPage
        return flowOf(
            representantes.values
                .drop(page * perPage)
                .take(perPage)
        )
    }

    override suspend fun findById(id: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando representante con id: $id" }

        // Buscamos
        return@withContext representantes[id]
    }


    override fun findByNombre(nombre: String): Flow<List<Representante>> {
        logger.debug { "findByNombre: Buscando representante con nombre: $nombre" }

        return flow { representantes.values.filter { it.nombre == nombre } }
    }


    override suspend fun save(entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando representante: $entity" }

        val time = LocalDateTime.now()
        val representante =
            entity.copy(createdAt = time, updatedAt = time)
        representantes[representante.id] = representante
        return@withContext representante

    }

    override suspend fun update(id: UUID, entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando representante: $entity" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Actualizamos los datos
        val representanteUpdate = entity.copy(
            nombre = entity.nombre,
            email = entity.email,
            updatedAt = LocalDateTime.now()
        )
        representantes[id] = representanteUpdate
        return@withContext representanteUpdate
    }

    override suspend fun delete(id: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Borrando representante con id: $id" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Borramos
        return@withContext representantes.remove(id)
    }
}