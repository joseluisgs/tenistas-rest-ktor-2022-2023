package joseluisgs.es.repositories.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaquetasRepository : CrudRepository<Raqueta, UUID> {
    suspend fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Raqueta>
    suspend fun findByMarca(marca: String): Flow<Raqueta>
}