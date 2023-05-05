package joseluisgs.es.services.users

import com.github.michaelbull.result.Result
import joseluisgs.es.errors.UserError
import joseluisgs.es.models.User
import kotlinx.coroutines.flow.Flow
import java.util.*

interface UsersService {
    suspend fun findAll(limit: Int?): Flow<User>

    suspend fun findById(id: UUID): Result<User, UserError>
    suspend fun findByUsername(username: String): Result<User, UserError>
    suspend fun checkUserNameAndPassword(username: String, password: String): Result<User, UserError>
    suspend fun save(entity: User): Result<User, UserError>
    suspend fun update(id: UUID, entity: User): Result<User, UserError>
    suspend fun delete(id: UUID): User?
    suspend fun isAdmin(id: UUID): Result<Boolean, UserError>
}