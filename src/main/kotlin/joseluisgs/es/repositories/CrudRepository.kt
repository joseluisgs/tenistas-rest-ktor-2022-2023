package joseluisgs.es.repositories

import kotlinx.coroutines.flow.Flow

// Importante voy a usar los nulos para evitar usar muchos errores y execepciones
// Al poder usar la nulabilidad como algo expecional puedo usar el operador ?
interface CrudRepository<T, ID> {
    suspend fun findAll(): Flow<T>
    suspend fun findById(id: ID): T?
    suspend fun save(entity: T): T
    suspend fun update(id: ID, entity: T): T?
    suspend fun delete(entity: T): T?
}