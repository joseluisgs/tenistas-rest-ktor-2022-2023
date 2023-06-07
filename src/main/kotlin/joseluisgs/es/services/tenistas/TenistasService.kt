package joseluisgs.es.services.tenistas

import com.github.michaelbull.result.Result
import joseluisgs.es.errors.TenistaError
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Tenista
import joseluisgs.es.models.TenistasNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import java.util.*

interface TenistasService {
    suspend fun findAll(): Flow<Tenista>
    suspend fun findAllPageable(page: Int, perPage: Int): Flow<Tenista>
    suspend fun findById(id: UUID): Result<Tenista, TenistaError>
    suspend fun findByNombre(nombre: String): Flow<Tenista>
    suspend fun findByRanking(ranking: Int): Result<Tenista, TenistaError>
    suspend fun save(tenista: Tenista): Result<Tenista, TenistaError>
    suspend fun update(id: UUID, tenista: Tenista): Result<Tenista, TenistaError>
    suspend fun delete(id: UUID): Result<Tenista, TenistaError>
    suspend fun findRaqueta(raquetaId: UUID?): Result<Raqueta?, TenistaError>

    // Estado de notificación
    val notificationState: SharedFlow<TenistasNotification>
}