package joseluisgs.es.services.tenistas

import joseluisgs.es.exceptions.RaquetaNotFoundException
import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.exceptions.TenistaNotFoundException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.*
import joseluisgs.es.repositories.raquetas.RaquetasRepository
import joseluisgs.es.repositories.tenistas.TenistasRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
class TenistasServiceImpl(
    @Named("TenistasCachedRepository")  // Repositorio de Tenistas Cacheado
    private val repository: TenistasRepository,
    @Named("RaquetasCachedRepository")  // Repositorio de Raquetas Cacheado
    private val raquetasRepository: RaquetasRepository
) : TenistasService {

    init {
        logger.debug { "Inicializando el servicio de tenistas" }
    }

    override suspend fun findAll(): Flow<Tenista> {
        logger.debug { "findAll: Buscando todos los tenistas en servicio" }

        return repository.findAll()
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todos los tenistas en servicio con página: $page y cantidad: $perPage" }

        return@withContext repository.findAllPageable(page, perPage)
    }

    override suspend fun findById(id: UUID): Tenista {
        logger.debug { "findById: Buscando tenista en servicio con id: $id" }

        return repository.findById(id)
            ?: throw TenistaNotFoundException("No se ha encontrado el tenista con id: $id")

    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> {
        logger.debug { "findByNombre: Buscando tenistas en servicio con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    override suspend fun findByRanking(ranking: Int): Tenista? {
        logger.debug { "findByRanking: Buscando tenistas en servicio con ranking: $ranking" }

        return repository.findByRanking(ranking)
    }

    override suspend fun save(tenista: Tenista): Tenista {
        logger.debug { "create: Creando tenistas en servicio" }

        // Insertamos el representante y devolvemos el resultado y avisa a los subscriptores
        return repository.save(tenista)
            .also { onChange(Notificacion.Tipo.CREATE, it.id, it) }
    }

    override suspend fun update(id: UUID, tenista: Tenista): Tenista {
        logger.debug { "update: Actualizando tenista en servicio" }

        val existe = repository.findById(id)

        existe?.let {
            return repository.update(id, tenista)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.id, it) }!!
        } ?: throw RepresentanteNotFoundException("No se ha encontrado el tenista con id: $id")
    }

    override suspend fun delete(id: UUID): Tenista {
        logger.debug { "delete: Borrando tenista en servicio" }

        val existe = repository.findById(id)

        existe?.let {
            // meto el try catch para que no se caiga la aplicación si no se puede borrar por tener raquetas asociadas
            return repository.delete(existe)
                .also { onChange(Notificacion.Tipo.DELETE, it!!.id, it) }!!

        } ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")

    }

    override suspend fun findRaqueta(id: UUID?): Raqueta? {
        logger.debug { "findRepresentante: Buscando raqueta en servicio" }

        id?.let {
            return raquetasRepository.findById(id)
                ?: throw RaquetaNotFoundException("No se ha encontrado el raqueta con id: $id")
        }
        return null
    }

    /// ---- Tiempo real, patrón observer!!!

    // Mis suscriptores, un mapa de codigo, con la función que se ejecutará
    // Si no te gusta usar la función como parámetro, puedes usar el objeto de la sesión (pero para eso Kotlin
    // es funcional ;)
    private val suscriptores =
        mutableMapOf<Int, suspend (TenistasNotification) -> Unit>()

    override fun addSuscriptor(id: Int, suscriptor: suspend (TenistasNotification) -> Unit) {
        logger.debug { "addSuscriptor: Añadiendo suscriptor con id: $id" }

        // Añadimos el suscriptor, que es la función que se ejecutará
        suscriptores[id] = suscriptor
    }

    override fun removeSuscriptor(id: Int) {
        logger.debug { "removeSuscriptor: Desconectando suscriptor con id: $" }

        suscriptores.remove(id)
    }

    // Se ejecuta en cada cambio
    private suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Tenista? = null) {
        logger.debug { "onChange: Cambio en Tenistas: $tipo, notificando a los suscriptores afectada entidad: $data" }

        // Por cada suscriptor, ejecutamos la función que se ha almacenado
        // Si almacenas el objeto de la sesión, puedes usar el método de la sesión, que es sendSerialized
        suscriptores.values.forEach {
            it.invoke(
                Notificacion(
                    tipo,
                    id,
                    data?.toDto(findRaqueta(data.raquetaId))
                )
            )
        }
    }
}