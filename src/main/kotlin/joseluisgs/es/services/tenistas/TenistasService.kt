package joseluisgs.es.services.tenistas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Tenista
import joseluisgs.es.models.TenistasNotification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TenistasService {
    suspend fun findAll(): Flow<Tenista>
    suspend fun findAllPageable(page: Int, perPage: Int): Flow<Tenista>
    suspend fun findById(id: UUID): Tenista
    suspend fun findByNombre(nombre: String): Flow<Tenista>
    suspend fun findByRanking(ranking: Int): Tenista
    suspend fun save(tenista: Tenista): Tenista
    suspend fun update(id: UUID, tenista: Tenista): Tenista
    suspend fun delete(id: UUID): Tenista
    suspend fun findRaqueta(raquetaId: UUID?): Raqueta?

    // Suscripción a cambios para notificar tiempo real
    fun addSuscriptor(id: Int, suscriptor: suspend (TenistasNotification) -> Unit)
    fun removeSuscriptor(id: Int)
}