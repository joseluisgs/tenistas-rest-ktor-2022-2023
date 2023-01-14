package joseluisgs.es.services.representantes

import joseluisgs.es.exceptions.RepresentanteException
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.representantes.RepresentantesRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class RepresentanteServiceImpl(
    private val repository: RepresentantesRepository
) : RepresentantesService {
    override fun findAll(): Flow<Representante> {
        logger.debug { "findAll: Buscando todos los representantes en servicio" }

        return repository.findAll()
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<Representante> {
        logger.debug { "findAllPageable: Buscando todos los representantes en servicio con página: $page y cantidad: $perPage" }

        return repository.findAllPageable(page, perPage)
    }

    override suspend fun findById(id: UUID): Representante {
        logger.debug { "findById: Buscando representante en servicio con id: $id" }

        return repository.findById(id)
            ?: throw RepresentanteException("No se ha encontrado el representante con id: $id")
    }

    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findByNombre: Buscando representante en servicio con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }


    override suspend fun save(representante: Representante): Representante {
        logger.debug { "create: Creando representante en servicio" }

        return repository.save(representante)
    }

    override suspend fun update(id: UUID, representante: Representante): Representante {
        logger.debug { "update: Actualizando representante en servicio" }

        return repository.update(id, representante)
    }

    override suspend fun delete(id: UUID) {
        logger.debug { "delete: Borrando representante en servicio" }

        repository.delete(id)
    }
}