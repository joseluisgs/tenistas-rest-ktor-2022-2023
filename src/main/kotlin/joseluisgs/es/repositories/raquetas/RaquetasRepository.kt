package joseluisgs.es.repositories.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RaquetasRepository : CrudRepository<Raqueta, UUID> {
    fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Raqueta>
    fun findByMarca(marca: String): Flow<Raqueta>

}