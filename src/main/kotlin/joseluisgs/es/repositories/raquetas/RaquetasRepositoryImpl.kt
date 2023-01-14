package joseluisgs.es.repositories.raquetas

import joseluisgs.es.db.getRaquetasInit
import joseluisgs.es.exceptions.RaquetaException
import joseluisgs.es.models.Raqueta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

class RaquetasRepositoryImpl : RaquetasRepository {

    // Fuente de datos
    private val raquetas: MutableMap<Long, Raqueta> = mutableMapOf()

    init {
        logger.debug { "Iniciando Repositorio de Raquetas" }

        getRaquetasInit().forEach {
            raquetas[it.id] = it
        }
    }

    override fun findAll(): Flow<Raqueta> {
        logger.debug { "findAll: Buscando todas las raquetas" }

        // Filtramos por página y por perPage
        return raquetas.values.asFlow()
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta> {
        logger.debug { "findAllPageable: Buscando todas las raquetas con página: $page y cantidad: $perPage" }

        // Filtramos por página y por perPage
        return raquetas.values
            .drop(page * perPage)
            .take(perPage)
            .asFlow()
    }

    override suspend fun findByUuid(uuid: UUID): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "findByUuid: Buscando raqueta con uuid: $uuid" }

        return@withContext raquetas.values.find { it.uuid == uuid }
    }

    override fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "findByMarca: Buscando raqueta con marca: $marca" }

        return raquetas.values.filter { it.marca == marca }.asFlow()
    }

    override suspend fun findById(id: Long): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando raqueta con id: $id" }

        // Buscamos
        return@withContext raquetas[id] ?: throw RaquetaException("No existe la raqueta con id $id")
    }

    override suspend fun save(entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando raqueta: $entity" }

        // Debo saber el ultimo id para poder añadir uno nuevo
        val id = raquetas.keys.maxOrNull() ?: 0
        // Le sumamos 1 al id
        val raqueta = entity.copy(id = id + 1, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        raquetas[raqueta.id] = raqueta
        return@withContext raqueta

    }

    override suspend fun update(id: Long, entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando raqueta: $entity" }

        // Buscamos la raqueta
        val raqueta = findById(id)
        // Actualizamos los datos
        raquetas[id] = raqueta.copy(
            marca = entity.marca,
            precio = entity.precio,
            represetanteId = entity.represetanteId,
            updatedAt = LocalDateTime.now()
        )
        return@withContext raquetas[id]!!
    }

    override suspend fun delete(id: Long): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "delete: Guardando raqueta: $id" }

        // Buscamos
        val raqueta = findById(id)
        // Borramos
        raquetas.remove(id)
        return@withContext raqueta
    }
}