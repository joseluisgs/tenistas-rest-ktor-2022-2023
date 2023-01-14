package joseluisgs.es.repositories.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaquetasRepository : CrudRepository<Raqueta, Long> {
    fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Raqueta>
    suspend fun findByUuid(uuid: UUID): Raqueta?
    fun findByMarca(marca: String): Flow<Raqueta>

}