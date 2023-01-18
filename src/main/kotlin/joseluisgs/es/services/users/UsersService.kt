package joseluisgs.es.services.users

import joseluisgs.es.models.User
import kotlinx.coroutines.flow.Flow
import java.util.*

interface UsersService {
    suspend fun findAll(limit: Int?): Flow<User>

    suspend fun findById(id: UUID): User
    suspend fun findByUsername(username: String): User?
    fun hashedPassword(password: String): String
    suspend fun checkUserNameAndPassword(username: String, password: String): User?
    suspend fun save(entity: User): User
    suspend fun update(id: UUID, entity: User): User?
    suspend fun delete(id: UUID): User?
}