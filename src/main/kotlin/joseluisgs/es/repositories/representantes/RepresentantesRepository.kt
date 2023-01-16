package joseluisgs.es.repositories.representantes

import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RepresentantesRepository : CrudRepository<Representante, UUID> {
    fun findAllPageable(page: Int = 0, perPage: Int = 10): Flow<Representante>
    fun findByNombre(nombre: String): Flow<Representante>

}