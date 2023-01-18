package joseluisgs.es.repositories.representantes

import joseluisgs.es.db.getRepresentantesInit
import joseluisgs.es.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
@Named("PersonasRepository")
class RepresentantesRepositoryImpl : RepresentantesRepository {

    // Fuente de datos
    private val representantes: MutableMap<UUID, Representante> = mutableMapOf()

    init {
        logger.debug { "Iniciando Repositorio de Representantes" }

        getRepresentantesInit().forEach {
            representantes[it.id] = it
        }
    }

    override suspend fun findAll(): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los representantes" }

        // Filtramos por página y por perPage
        return@withContext representantes.values.toList().asFlow()
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todos los representantes con página: $page y cantidad: $perPage" }

        // Filtramos por página y por perPage
        return@withContext representantes.values
            .drop(page * perPage)
            .take(perPage)
            .asFlow()
    }

    override suspend fun findById(id: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando representante con id: $id" }

        // Buscamos
        return@withContext representantes[id]
    }


    override suspend fun findByNombre(nombre: String): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findByNombre: Buscando representante con nombre: $nombre" }

        return@withContext representantes.values.filter { it.nombre.lowercase().contains(nombre.lowercase()) }.asFlow()
    }


    override suspend fun save(entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando representante: $entity" }

        representantes[entity.id] = entity
        return@withContext entity

    }

    override suspend fun update(id: UUID, entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando representante: $entity" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Actualizamos los datos
        val representanteUpdate = entity.copy(
            nombre = entity.nombre,
            email = entity.email,
            createdAt = entity.createdAt,
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