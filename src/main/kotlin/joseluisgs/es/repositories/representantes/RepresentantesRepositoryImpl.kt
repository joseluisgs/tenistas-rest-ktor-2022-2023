package joseluisgs.es.repositories.representantes

import joseluisgs.es.db.getRepresentantesInit
import joseluisgs.es.exceptions.RaquetaException
import joseluisgs.es.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class RepresentantesRepositoryImpl : RepresentantesRepository {

    // Fuente de datos
    private val representantes: MutableMap<Long, Representante> = mutableMapOf()

    init {
        getRepresentantesInit().forEach {
            representantes[it.id] = it
        }
    }

    override fun findAll(): Flow<Representante> {
        return representantes.values.asFlow()
    }

    override fun findByNombre(nombre: String): Flow<Representante> {
        return representantes.values.filter { it.nombre == nombre }.asFlow()
    }

    override suspend fun findById(id: Long): Representante = withContext(Dispatchers.IO) {
        // Buscamos
        return@withContext representantes[id] ?: throw RaquetaException("No existe el representante con id $id")
    }

    override suspend fun save(entity: Representante): Representante = withContext(Dispatchers.IO) {
        // Debo saber el ultimo id para poder añadir uno nuevo
        val id = representantes.keys.maxOrNull() ?: 0
        // Le sumamos 1 al id
        val representante = entity.copy(id = id + 1, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        representantes[representante.id] = representante
        return@withContext representante

    }

    override suspend fun update(id: Long, entity: Representante): Representante = withContext(Dispatchers.IO) {
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
        // Buscamos
        val representante = findById(id)
        // Borramos
        representantes.remove(id)
        return@withContext representante
    }
}