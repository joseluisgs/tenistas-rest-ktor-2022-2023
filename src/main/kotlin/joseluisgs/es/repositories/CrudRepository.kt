package joseluisgs.es.repositories

import kotlinx.coroutines.flow.Flow

interface CrudRepository<T, ID> {
    suspend fun findAll(): Flow<List<T>>
    suspend fun findById(id: ID): T?
    suspend fun save(entity: T): T
    suspend fun update(id: ID, entity: T): T
    suspend fun delete(id: ID): T
}