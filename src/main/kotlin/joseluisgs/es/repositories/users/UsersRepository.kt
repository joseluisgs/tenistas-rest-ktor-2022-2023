package joseluisgs.es.repositories.users

import joseluisgs.es.models.User
import joseluisgs.es.repositories.CrudRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

interface UsersRepository : CrudRepository<User, UUID> {
    suspend fun findAll(limit: Int?): Flow<User>
    suspend fun findByUsername(username: String): User?
    fun hashedPassword(password: String): String
    suspend fun checkUserNameAndPassword(username: String, password: String): User?
}

