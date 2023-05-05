package joseluisgs.es.services.representantes

import com.github.michaelbull.result.Result
import joseluisgs.es.errors.RepresentanteError
import joseluisgs.es.models.Representante
import joseluisgs.es.models.RepresentantesNotification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RepresentantesService {
    suspend fun findAll(): Flow<Representante>
    suspend fun findAllPageable(page: Int, perPage: Int): Flow<Representante>
    suspend fun findById(id: UUID): Result<Representante, RepresentanteError>
    suspend fun findByNombre(nombre: String): Flow<Representante>
    suspend fun save(representante: Representante): Result<Representante, RepresentanteError>
    suspend fun update(id: UUID, representante: Representante): Result<Representante, RepresentanteError>
    suspend fun delete(id: UUID): Result<Representante, RepresentanteError>

    // Suscripción a cambios para notificar tiempo real
    fun addSuscriptor(id: Int, suscriptor: suspend (RepresentantesNotification) -> Unit)
    fun removeSuscriptor(id: Int)
}