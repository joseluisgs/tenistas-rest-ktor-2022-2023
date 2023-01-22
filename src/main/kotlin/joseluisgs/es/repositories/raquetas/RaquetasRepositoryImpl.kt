package joseluisgs.es.repositories.raquetas

import joseluisgs.es.db.getRaquetasInit
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
    private val raquetas: MutableMap<UUID, Raqueta> = mutableMapOf()

    init {
        logger.debug { "Iniciando Repositorio de Raquetas" }

        getRaquetasInit().forEach {
            raquetas[it.id] = it
        }
    }

    override suspend fun findAll(): Flow<Raqueta> {
        logger.debug { "findAll: Buscando todas las raquetas" }

        // Filtramos por página y por perPage
        return raquetas.values.toList().asFlow()
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta> {
        logger.debug { "findAllPageable: Buscando todas las raquetas con página: $page y cantidad: $perPage" }

        // Filtramos por página y por perPage
        return raquetas.values
            .drop(page * perPage)
            .take(perPage)
            .asFlow()

    }

    override suspend fun findById(id: UUID): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando raqueta con id: $id" }

        // Buscamos
        return@withContext raquetas[id]
    }

    override fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "findByMarca: Buscando raqueta con marca: $marca" }

        return raquetas.values.filter { it.marca.lowercase().contains(marca.lowercase()) }.asFlow()
    }

    override suspend fun save(entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando raqueta: $entity" }

        val raqueta =
            entity.copy(id = UUID.randomUUID(), createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        raquetas[raqueta.id] = raqueta
        return@withContext raqueta

    }

    override suspend fun update(id: UUID, entity: Raqueta): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando raqueta: $entity" }

        // Buscamos la raqueta
        val raqueta = findById(id)
        // Si existe la actualizamos
        raqueta?.let {
            val raquetaActualizada =
                entity.copy(id = id, marca = entity.marca, precio = entity.precio, updatedAt = LocalDateTime.now())
            raquetas[id] = raquetaActualizada
            return@withContext raquetaActualizada
        }

    }

    override suspend fun delete(entity: Raqueta): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Guardando raqueta: ${entity.id}" }

        // Buscamos
        val raqueta = findById(entity.id)
        raqueta?.let {
            raquetas.remove(entity.id)
            return@withContext it
        }
    }
}