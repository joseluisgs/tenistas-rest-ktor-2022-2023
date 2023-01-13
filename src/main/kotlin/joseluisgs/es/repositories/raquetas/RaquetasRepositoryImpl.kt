package joseluisgs.es.repositories.raquetas

import joseluisgs.es.db.getRaquetasInit
import joseluisgs.es.exceptions.RaquetaException
import joseluisgs.es.models.Raqueta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class RaquetasRepositoryImpl : RaquetasRepository {

    // Fuente de datos
    private val raquetas: MutableMap<Long, Raqueta> = mutableMapOf()

    init {
        getRaquetasInit().forEach {
            raquetas[it.id] = it
        }
    }

    override fun findAll(): Flow<Raqueta> {
        return raquetas.values.asFlow()
    }

    override fun findByMarca(marca: String): Flow<Raqueta> {
        return raquetas.values.filter { it.marca == marca }.asFlow()
    }

    override suspend fun findById(id: Long): Raqueta = withContext(Dispatchers.IO) {
        // Buscamos
        return@withContext raquetas[id] ?: throw RaquetaException("No existe la raqueta con id $id")
    }

    override suspend fun save(entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        // Debo saber el ultimo id para poder añadir uno nuevo
        val id = raquetas.keys.maxOrNull() ?: 0
        // Le sumamos 1 al id
        val raqueta = entity.copy(id = id + 1, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        raquetas[raqueta.id] = raqueta
        return@withContext raqueta

    }

    override suspend fun update(id: Long, entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
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
        // Buscamos
        val raqueta = findById(id)
        // Borramos
        raquetas.remove(id)
        return@withContext raqueta
    }
}