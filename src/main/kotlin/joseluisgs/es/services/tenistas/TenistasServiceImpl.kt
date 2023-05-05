package joseluisgs.es.services.tenistas


import com.github.michaelbull.result.*
import joseluisgs.es.errors.TenistaError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.Notificacion
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Tenista
import joseluisgs.es.models.TenistasNotification
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

    override suspend fun findById(id: UUID): Result<Tenista, TenistaError> {
        logger.debug { "findById: Buscando tenista en servicio con id: $id" }

        return repository.findById(id)
            ?.let { Ok(it) }
            ?: Err(TenistaError.NotFound("No se ha encontrado el tenista con id: $id"))
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> {
        logger.debug { "findByNombre: Buscando tenistas en servicio con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    override suspend fun findByRanking(ranking: Int): Result<Tenista, TenistaError> {
        logger.debug { "findByRanking: Buscando tenistas en servicio con ranking: $ranking" }

        return repository.findByRanking(ranking)
            ?.let { Ok(it) }
            ?: Err(TenistaError.NotFound("No se ha encontrado el tenista con ranking: $ranking"))
    }

    override suspend fun save(tenista: Tenista): Result<Tenista, TenistaError> {
        logger.debug { "create: Creando tenistas en servicio" }

        // Es verdad cuando falla, pues no existe el el ranking!!!
        return findByRanking(tenista.ranking).onSuccess {
            // Si no hay otro con el mismo ranking, error
            return Err(TenistaError.BadRequest("Ya existe un tenista con el ranking: ${tenista.ranking} y es: ${it.nombre}"))
        }.onFailure {
            // Hsy otro con el mismo tanking??
            return findRaqueta(tenista.raquetaId).andThen {
                Ok(repository.save(tenista)).also {
                    onChange(Notificacion.Tipo.CREATE, it.value.id, it.value)
                }
            }
        }
    }

    override suspend fun update(id: UUID, tenista: Tenista): Result<Tenista, TenistaError> {
        logger.debug { "update: Actualizando tenista en servicio" }

        return findById(id).andThen {
            findRaqueta(tenista.raquetaId)
        }.andThen {
            // Buscamos si hay otro con el mismo ranking
            findByRanking(tenista.ranking).onSuccess {
                // Si el id es el mismo, no hay problema, soy yo!!!
                if (it.id != id) {
                    return Err(TenistaError.BadRequest("Ya existe un tenista con el ranking: ${tenista.ranking} y es: ${it.nombre}"))
                }
            }
            // Si no hay otro con el mismo ranking, actualizamos
            return Ok(repository.update(id, tenista)!!).also {
                onChange(Notificacion.Tipo.UPDATE, it.value.id, it.value)
            }
        }
    }

    override suspend fun delete(id: UUID): Result<Tenista, TenistaError> {
        logger.debug { "delete: Borrando tenista en servicio" }

        return findById(id).andThen {
            Ok(repository.delete(it)!!).also { r ->
                onChange(Notificacion.Tipo.DELETE, r.value.id, r.value)
            }
        }
    }

    override suspend fun findRaqueta(raquetaId: UUID?): Result<Raqueta?, TenistaError> {
        logger.debug { "findRaqueta: Buscando raqueta en servicio" }

        raquetaId?.let {
            return raquetasRepository.findById(it)
                ?.let { r -> Ok(r) }
                ?: Err(TenistaError.RaquetaNotFound("No se ha encontrado la raqueta con id: $raquetaId"))
        } ?: return Ok(null)
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
                    "TENISTA",
                    tipo,
                    id,
                    data?.toDto(findRaqueta(data.raquetaId).get())
                )
            )
        }
    }
}