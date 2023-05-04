package joseluisgs.es.services.raquetas

import joseluisgs.es.exceptions.RaquetaConflictIntegrityException
import joseluisgs.es.exceptions.RaquetaNotFoundException
import joseluisgs.es.exceptions.RepresentanteException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.Notificacion
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.RaquetasNotification
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.raquetas.RaquetasRepository
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

    override suspend fun findById(id: UUID): Raqueta {
        logger.debug { "findById: Buscando raqueta en servicio con id: $id" }

        // return repository.findById(id) ?: throw NoSuchElementException("No se ha encontrado el representante con id: $id")
        return repository.findById(id)
            ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con id: $id")

    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "findByNombre: Buscando raqueta en servicio con marca: $marca" }

        return repository.findByMarca(marca)
    }


    override suspend fun save(raqueta: Raqueta): Raqueta {
        logger.debug { "create: Creando raqueta en servicio" }

        // Existe el representante!
        val representante = findRepresentante(raqueta.representanteId)

        // Insertamos el representante y devolvemos el resultado y avisa a los subscriptores
        return repository.save(raqueta)
            .also { onChange(Notificacion.Tipo.CREATE, it.id, it) }
    }

    override suspend fun update(id: UUID, raqueta: Raqueta): Raqueta {
        logger.debug { "update: Actualizando raqueta en servicio" }

        val existe = repository.findById(id)

        // Existe el representante!
        val representante = findRepresentante(raqueta.representanteId)

        existe?.let {
            return repository.update(id, raqueta)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.id, it) }!!
        } ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con id: $id")
    }

    override suspend fun delete(id: UUID): Raqueta {
        logger.debug { "delete: Borrando raqueta en servicio" }

        val existe = repository.findById(id)

        existe?.let {
            // meto el try catch para que no se caiga la aplicación si no se puede borrar por tener raquetas asociadas
            try {
                return repository.delete(existe)
                    .also { onChange(Notificacion.Tipo.DELETE, it!!.id, it) }!!
            } catch (e: Exception) {
                throw RaquetaConflictIntegrityException("No se puede borrar la raqueta con id: $id porque tiene tenistas asociados")
            }
        } ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con id: $id")
    }

    override suspend fun findRepresentante(id: UUID): Representante {
        logger.debug { "findRepresentante: Buscando representante en servicio" }

        return representantesRepository.findById(id)
            ?: throw RepresentanteException.NotFoundException("No se ha encontrado el representante con id: $id")
    }

    /// ---- Tiempo real, patrón observer!!!

    // Mis suscriptores, un mapa de codigo, con la función que se ejecutará
    // Si no te gusta usar la función como parámetro, puedes usar el objeto de la sesión (pero para eso Kotlin
    // es funcional ;)
    private val suscriptores =
        mutableMapOf<Int, suspend (RaquetasNotification) -> Unit>()

    override fun addSuscriptor(id: Int, suscriptor: suspend (RaquetasNotification) -> Unit) {
        logger.debug { "addSuscriptor: Añadiendo suscriptor con id: $id" }

        // Añadimos el suscriptor, que es la función que se ejecutará
        suscriptores[id] = suscriptor
    }

    override fun removeSuscriptor(id: Int) {
        logger.debug { "removeSuscriptor: Desconectando suscriptor con id: $" }

        suscriptores.remove(id)
    }

    // Se ejecuta en cada cambio
    private suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Raqueta? = null) {
        logger.debug { "onChange: Cambio en Raquetas: $tipo, notificando a los suscriptores afectada entidad: $data" }

        val myScope = CoroutineScope(Dispatchers.IO)
        // Por cada suscriptor, ejecutamos la función que se ha almacenado
        // Si almacenas el objeto de la sesión, puedes usar el método de la sesión, que es sendSerialized
        myScope.launch {
            suscriptores.values.forEach {
                it.invoke(
                    RaquetasNotification(
                        "RAQUETA",
                        tipo,
                        id,
                        data?.toDto(findRepresentante(data.representanteId))
                    )
                )
            }
        }
    }
}