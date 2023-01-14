package joseluisgs.es.repositories.representantes

import joseluisgs.es.db.getRepresentantesInit
import joseluisgs.es.exceptions.RaquetaException
import joseluisgs.es.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

class RepresentantesRepositoryImpl : RepresentantesRepository {

    // Fuente de datos
    private val representantes: MutableMap<Long, Representante> = mutableMapOf()

    init {
        logger.debug { "Iniciando Repositorio de Representantes" }

        getRepresentantesInit().forEach {
            representantes[it.id] = it
        }
    }

    override fun findAll(): Flow<Representante> {
        logger.debug { "findAll: Buscando todos los representantes" }

        // Filtramos por página y por perPage
        return representantes.values.asFlow()
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<Representante> {
        logger.debug { "findAllPageable: Buscando todos los representantes con página: $page y cantidad: $perPage" }

        // Filtramos por página y por perPage
        return representantes.values
            .drop(page * perPage)
            .take(perPage)
            .asFlow()
    }

    override suspend fun findByUuid(uuid: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "findByUuid: Buscando representante con uuid: $uuid" }

        return@withContext representantes.values.find { it.uuid == uuid }
    }


    override fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findByNombre: Buscando representante con nombre: $nombre" }

        return representantes.values.filter { it.nombre == nombre }.asFlow()
    }

    override suspend fun findById(id: Long): Representante = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando representante con id: $id" }

        // Buscamos
        return@withContext representantes[id] ?: throw RaquetaException("No existe el representante con id $id")
    }

    override suspend fun save(entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando representante: $entity" }

        // Debo saber el ultimo id para poder añadir uno nuevo
        val id = representantes.keys.maxOrNull() ?: 0
        // Le sumamos 1 al id
        val representante = entity.copy(id = id + 1, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        representantes[representante.id] = representante
        return@withContext representante

    }

    override suspend fun update(id: Long, entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando representante: $entity" }

        // Buscamos
        val representante = findById(id)
        // Actualizamos los datos
        representantes[id] = representante.copy(
            nombre = entity.nombre,
            email = entity.email,
            updatedAt = LocalDateTime.now()
        )
        return@withContext representantes[id]!!
    }

    override suspend fun delete(id: Long): Representante = withContext(Dispatchers.IO) {
        logger.debug { "delete: Borrando representante con id: $id" }

        // Buscamos
        val representante = findById(id)
        // Borramos
        representantes.remove(id)
        return@withContext representante
    }
}