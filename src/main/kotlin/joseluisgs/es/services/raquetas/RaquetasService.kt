package joseluisgs.es.services.raquetas

import com.github.michaelbull.result.Result
import joseluisgs.es.errors.RaquetaError
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.RaquetasNotification
import joseluisgs.es.models.Representante
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaquetasService {
    suspend fun findAll(): Flow<Raqueta>
    suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta>
    suspend fun findById(id: UUID): Result<Raqueta, RaquetaError>
    suspend fun findByMarca(marca: String): Flow<Raqueta>
    suspend fun save(raqueta: Raqueta): Result<Raqueta, RaquetaError>
    suspend fun update(id: UUID, raqueta: Raqueta): Result<Raqueta, RaquetaError>
    suspend fun delete(id: UUID): Result<Raqueta, RaquetaError>
    suspend fun findRepresentante(id: UUID): Result<Representante, RaquetaError>

    // Suscripción a cambios para notificar tiempo real
    fun addSuscriptor(id: Int, suscriptor: suspend (RaquetasNotification) -> Unit)
    fun removeSuscriptor(id: Int)
}