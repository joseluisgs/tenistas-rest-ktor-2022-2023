package joseluisgs.es.services.raquetas

import com.github.michaelbull.result.*
import joseluisgs.es.errors.RaquetaError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.Notificacion
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.RaquetasNotification
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.raquetas.RaquetasRepository
import joseluisgs.es.repositories.representantes.RepresentantesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
// @Named("RepresentantesService")
class RaquetasServiceImpl(
    @Named("RaquetasCachedRepository")  // Repositorio de Representantes Cacheado
    private val repository: RaquetasRepository,
    @Named("RepresentantesCachedRepository")  // Repositorio de Representantes Cacheado
    private val representantesRepository: RepresentantesRepository
) : RaquetasService {

    init {
        logger.debug { "Inicializando el servicio de raquetas" }
    }

    override suspend fun findAll(): Flow<Raqueta> {
        logger.debug { "findAll: Buscando todas las raquetas en servicio" }

        return repository.findAll()
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todas las raquetas en servicio con página: $page y cantidad: $perPage" }

        return@withContext repository.findAllPageable(page, perPage)
    }

    override suspend fun findById(id: UUID): Result<Raqueta, RaquetaError> {
        logger.debug { "findById: Buscando raqueta en servicio con id: $id" }

        return repository.findById(id)
            ?.let { Ok(it) }
            ?: Err(RaquetaError.NotFound("No se ha encontrado la raqueta con id: $id"))
    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "findByNombre: Buscando raqueta en servicio con marca: $marca" }

        return repository.findByMarca(marca)
    }


    override suspend fun save(raqueta: Raqueta): Result<Raqueta, RaquetaError> {
        logger.debug { "create: Creando raqueta en servicio" }

        return findRepresentante(raqueta.representanteId).andThen {
            Ok(repository.save(raqueta)).also {
                onChange(Notificacion.Tipo.CREATE, it.value.id, it.value)
            }
        }
    }

    override suspend fun update(id: UUID, raqueta: Raqueta): Result<Raqueta, RaquetaError> {
        logger.debug { "update: Actualizando raqueta en servicio" }

        return findById(id).andThen {
            findRepresentante(raqueta.representanteId)
        }.andThen {
            Ok(repository.update(id, raqueta)!!).also {
                onChange(Notificacion.Tipo.UPDATE, it.value.id, it.value)
            }
        }
    }

    override suspend fun delete(id: UUID): Result<Raqueta, RaquetaError> {
        logger.debug { "delete: Borrando raqueta en servicio" }

        return findById(id).andThen {
            try {
                Ok(repository.delete(it)!!).also { r ->
                    onChange(Notificacion.Tipo.DELETE, r.value.id, r.value)
                }
            } catch (e: Exception) {
                Err(RaquetaError.ConflictIntegrity("No se puede borrar la raqueta con id: $id porque tiene tenistas asociados"))
            }
        }
    }

    override suspend fun findRepresentante(id: UUID): Result<Representante, RaquetaError> {
        logger.debug { "findRepresentante: Buscando representante en servicio" }

        return representantesRepository.findById(id)
            ?.let { Ok(it) }
            ?: Err(RaquetaError.RepresentanteNotFound("No se ha encontrado el representante con id: $id"))
    }

    /// ---- Tiempo real, estado reactivo ----
    private val _notificationState: MutableStateFlow<RaquetasNotification> = MutableStateFlow(
        RaquetasNotification(
            entity = "",
            tipo = Notificacion.Tipo.OTHER,
            id = null,
            data = null
        )
    )
    override val notificationState: StateFlow<RaquetasNotification> = _notificationState

    private suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Raqueta) {
        logger.debug { "onChange: Cambio en Raquetas: $tipo, notificando a los suscriptores afectada entidad: $data" }
        // actualizamos el estado
        _notificationState.value = RaquetasNotification(
            entity = "RAQUETA",
            tipo = tipo,
            id = id,
            data = data.toDto(findRepresentante(data.representanteId).get()!!)
        )
    }
}