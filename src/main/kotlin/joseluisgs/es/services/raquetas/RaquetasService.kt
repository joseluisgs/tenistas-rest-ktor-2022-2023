package joseluisgs.es.services.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.RaquetasNotification
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaquetasService {
    suspend fun findAll(): Flow<Raqueta>
    suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta>
    suspend fun findById(id: UUID): Raqueta
    suspend fun findByMarca(marca: String): Flow<Raqueta>
    suspend fun save(raqueta: Raqueta): Raqueta
    suspend fun update(id: UUID, raqueta: Raqueta): Raqueta
    suspend fun delete(id: UUID): Raqueta

    // Suscripción a cambios para notificar tiempo real
    fun addSuscriptor(id: Int, suscriptor: suspend (RaquetasNotification) -> Unit)
    fun removeSuscriptor(id: Int)
}