package joseluisgs.es.services.representantes

import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.models.Notificacion
import joseluisgs.es.models.Representante
import joseluisgs.es.models.RepresentantesNotification
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

        // Insertamos el representante y devolvemos el resultado y avisa a los subscriptores
        return repository.save(representante)
            .also { onChange(Notificacion.Tipo.CREATE, it.id, it) }
    }

    override suspend fun update(id: UUID, representante: Representante): Representante {
        logger.debug { "update: Actualizando representante en servicio" }

        // Actualizamos el representante y devolvemos el resultado y avisa a los subscriptores
        return repository.update(id, representante)
            ?.also { onChange(Notificacion.Tipo.UPDATE, it.id, it) }
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    override suspend fun delete(id: UUID): Representante {
        logger.debug { "delete: Borrando representante en servicio" }

        return repository.delete(id)
            ?.also { onChange(Notificacion.Tipo.DELETE, it.id) }
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    /// ---- Tiempo real, patrón observer!!!

    // Mis suscriptores, un mapa de codigo, con la función que se ejecutará
    private val suscriptores =
        mutableMapOf<Int, suspend (RepresentantesNotification) -> Unit>()

    override fun addSuscriptor(id: Int, suscriptor: suspend (RepresentantesNotification) -> Unit) {
        logger.debug { "addSuscriptor: Añadiendo suscriptor con id: $id" }

        // Añadimos el suscriptor, que es la función que se ejecutará
        suscriptores[id] = suscriptor
    }

    override fun removeSuscriptor(id: Int) {
        logger.debug { "removeSuscriptor: Desconectando suscriptor con id: $" }

        suscriptores.remove(id)
    }

    // Se ejecuta en cada cambio
    private suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, entidad: Representante? = null) {
        logger.debug { "onChange: Cambio en Representantes: $tipo, notificando a los suscriptores afectada entidad: $entidad" }

        // Por cada suscriptor, ejecutamos la función que se ha almacenado
        suscriptores.values.forEach {
            it.invoke(
                Notificacion(
                    tipo,
                    id,
                    entidad
                )
            ) // llama a la función que se ejecutará en el suscriptor sendSerialized
        }
    }
}