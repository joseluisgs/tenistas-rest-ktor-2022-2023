package joseluisgs.es.services.representantes

import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.representantes.RepresentantesRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

class RepresentantesServiceImpl(
    private val repository: RepresentantesRepository
) : RepresentantesService {

    init {
        logger.debug { "Inicializando el servicio de representantes" }
    }

    override suspend fun findAll(): Flow<List<Representante>> {
        logger.debug { "findAll: Buscando todos los representantes en servicio" }

        return repository.findAll()
    }

    override fun findAllPageable(page: Int, perPage: Int): Flow<List<Representante>> {
        logger.debug { "findAllPageable: Buscando todos los representantes en servicio con página: $page y cantidad: $perPage" }

        return repository.findAllPageable(page, perPage)
    }

    override suspend fun findById(id: UUID): Representante {
        logger.debug { "findById: Buscando representante en servicio con id: $id" }

        // return repository.findById(id) ?: throw NoSuchElementException("No se ha encontrado el representante con id: $id")
        return repository.findById(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")

    }

    override suspend fun findByNombre(nombre: String): Flow<List<Representante>> {
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
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    override suspend fun delete(id: UUID): Representante {
        logger.debug { "delete: Borrando representante en servicio" }

        return repository.delete(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }
}