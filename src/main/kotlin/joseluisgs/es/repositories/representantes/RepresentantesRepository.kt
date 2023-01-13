package joseluisgs.es.repositories.representantes

import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow

interface RepresentantesRepository : CrudRepository<Representante, Long> {
    fun findByNombre(nombre: String): Flow<Representante>
}