package joseluisgs.es.services.representantes

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import joseluisgs.es.errors.RepresentanteError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.Notificacion
import joseluisgs.es.models.Representante
import joseluisgs.es.models.RepresentantesNotification
import joseluisgs.es.repositories.representantes.RepresentantesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
/**
 * Servicio de [Representante]
 * @property repository Repositorio de [Representante]
 * @constructor Crea el servicio de [Representante]
 * @see RepresentantesRepository
 */
class RepresentantesServiceImpl(
    @Named("RepresentantesCachedRepository")  // Repositorio de Representantes Cacheado
    private val repository: RepresentantesRepository
) : RepresentantesService {

    init {
        logger.debug { "Inicializando el servicio de representantes" }
    }

    /**
     * Devuelve un flujo de representantes
     * @return un flujo de [Representante]
     */
    override suspend fun findAll(): Flow<Representante> {
        logger.debug { "findAll: Buscando todos los representantes en servicio" }

        return repository.findAll()
    }

    /**
     * Devuelve un flujo de representantes de acuerdo a la página solicitada y su tamaño
     * @param page Número de página
     * @param perPage Número de elementos por página
     * @return un flujo de [Representantes]
     */
    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todos los representantes en servicio con página: $page y cantidad: $perPage" }

        return@withContext repository.findAllPageable(page, perPage)
    }

    /**
     * Busca un representante en base a su id
     * @param id representante id
     * @return representante en base a su id
     * @throws RepresentanteNotFoundException representante no encontrado con el identificador
     */
    override suspend fun findById(id: UUID): Result<Representante, RepresentanteError> {
        logger.debug { "findById: Buscando representante en servicio con id: $id" }

        return repository.findById(id)?.let {
            Ok(it)
        } ?: Err(RepresentanteError.NotFound("No se ha encontrado el representante con id: $id"))
    }

    /**
     * Busca representantes en base a su nombre
     * @param name representante nombre
     * @return Flow de [Representante]
     */
    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findByNombre: Buscando representante en servicio con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    /**
     * Salva un representante en el sistema de almacenamiento
     * @param representante Representante a salvar
     * @return Representante en el sistema de almacenamiento
     */
    override suspend fun save(representante: Representante): Result<Representante, RepresentanteError> {
        logger.debug { "create: Creando representante en servicio" }

        return Ok(repository.save(representante))
            .also { onChange(Notificacion.Tipo.CREATE, it.value.id, it.value) }
    }

    /**
     * Actualiza el representante en base a su identificador
     * @param id representante identificador
     * @param representante Representante a actualizar
     * @return Representante actualizado
     * @throws RepresentanteNotFoundException representante no encontrado con el identificador
     */
    override suspend fun update(id: UUID, representante: Representante): Result<Representante, RepresentanteError> {
        logger.debug { "update: Actualizando representante en servicio" }

        return findById(id).andThen {
            Ok(repository.update(id, representante)!!)
                .also { onChange(Notificacion.Tipo.UPDATE, it.value.id, it.value) }
        }
    }

    /**
     * Elimina representante en base a su identificador
     * @param id representante identificador
     * @return Representante eliminado
     * @throws RepresentanteNotFoundException representante no encontrado con el identificador
     * @throws RepresentanteConflictIntegrityException no se pudo eliminar debido a que tiene raquetas asociadas
     */
    override suspend fun delete(id: UUID): Result<Representante, RepresentanteError> {
        logger.debug { "delete: Borrando representante en servicio" }

        return findById(id).andThen { it ->
            try {
                Ok(repository.delete(it)!!)
                    .also { r -> onChange(Notificacion.Tipo.DELETE, r.value.id, r.value) }
            } catch (e: Exception) {
                Err(RepresentanteError.ConflictIntegrity("No se puede borrar el representante con id: $id porque tiene raquetas asociadas"))
            }
        }
    }

    /// ---- Tiempo real, patrón observer!!!

    // Mis suscriptores, un mapa de codigo, con la función que se ejecutará
    // Si no te gusta usar la función como parámetro, puedes usar el objeto de la sesión (pero para eso Kotlin
    // es funcional ;)
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
    private suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Representante? = null) {
        logger.debug { "onChange: Cambio en Representantes: $tipo, notificando a los suscriptores afectada entidad: $data" }

        // Por cada suscriptor, ejecutamos la función que se ha almacenado
        // Si almacenas el objeto de la sesión, puedes usar el método de la sesión, que es sendSerialized
        val myScope = CoroutineScope(Dispatchers.IO)
        myScope.launch {
            suscriptores.values.forEach {
                it.invoke(
                    RepresentantesNotification(
                        "REPRESENTANTE",
                        tipo,
                        id,
                        data?.toDto() // Convertimos a DTO
                    )
                )
            }
        }
    }
}