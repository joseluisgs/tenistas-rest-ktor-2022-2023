package joseluisgs.es.repositories.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow

interface RaquetasRepository : CrudRepository<Raqueta, Long> {
    fun findByMarca(marca: String): Flow<Raqueta>

}