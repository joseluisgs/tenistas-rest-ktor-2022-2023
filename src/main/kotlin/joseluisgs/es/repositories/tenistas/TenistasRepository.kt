package joseluisgs.es.repositories.tenistas

import joseluisgs.es.models.Tenista
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface TenistasRepository : CrudRepository<Tenista, UUID> {
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Tenista>

    suspend fun findByRanking(ranking: Int): Tenista?
    suspend fun findByNombre(nombre: String): Flow<Tenista>

}