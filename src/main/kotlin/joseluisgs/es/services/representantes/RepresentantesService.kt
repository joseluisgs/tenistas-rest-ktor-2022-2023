package joseluisgs.es.services.representantes

import joseluisgs.es.models.Representante
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RepresentantesService {
    suspend fun findAll(): Flow<List<Representante>>
    fun findAllPageable(page: Int, perPage: Int): Flow<List<Representante>>
    suspend fun findById(id: UUID): Representante
    suspend fun findByNombre(nombre: String): Flow<List<Representante>>
    suspend fun save(representante: Representante): Representante
    suspend fun update(id: UUID, representante: Representante): Representante
    suspend fun delete(id: UUID): Representante
}