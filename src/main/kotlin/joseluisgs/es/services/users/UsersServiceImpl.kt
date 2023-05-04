package joseluisgs.es.services.users


import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import joseluisgs.es.errors.UserError
import joseluisgs.es.models.User
import joseluisgs.es.repositories.users.UsersRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
class UsersServiceImpl(
    private val repository: UsersRepository
) : UsersService {

    init {
        logger.debug { "Inicializando el servicio de Usuarios" }
    }

    override suspend fun findAll(limit: Int?): Flow<User> {
        logger.debug { "findAll: Buscando todos los usuarios" }

        return repository.findAll(limit)
    }

    override suspend fun findById(id: UUID): Result<User, UserError> {
        logger.debug { "findById: Buscando usuario con id: $id" }

        return repository.findById(id)?.let {
            Ok(it)
        } ?: Err(UserError.NotFound("No se ha encontrado el usuario con id: $id"))
    }

    override suspend fun findByUsername(username: String): Result<User, UserError> {
        logger.debug { "findByUsername: Buscando usuario con username: $username" }

        return repository.findByUsername(username)?.let {
            Ok(it)
        } ?: Err(UserError.NotFound("No se ha encontrado el usuario con username: $username"))
    }


    override suspend fun checkUserNameAndPassword(username: String, password: String): Result<User, UserError> {
        logger.debug { "checkUserNameAndPassword: Comprobando el usuario y contraseña" }

        return repository.checkUserNameAndPassword(username, password)?.let {
            Ok(it)
        } ?: Err(UserError.NotFound("Nombre de usuario o contraseña incorrectos"))
    }

    override suspend fun save(entity: User): Result<User, UserError> {
        logger.debug { "save: Creando usuario" }

        repository.findByUsername(entity.username)?.let {
            return Err(UserError.BadRequest("Ya existe un usuario con username: ${entity.username}"))
        } ?: run {
            return Ok(
                repository.save(
                    entity.copy(
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now(),
                        password = repository.hashedPassword(entity.password)
                    )
                )
            )
        }
    }

    override suspend fun update(id: UUID, entity: User): Result<User, UserError> {
        logger.debug { "update: Actualizando usuario con id: $id" }

        // buscamos en el repositorio el nombre nuevo de usuario
        val updated = repository.findByUsername(entity.username)
            ?: return Ok(repository.update(id, entity.copy(updatedAt = LocalDateTime.now()))!!)
        // si existe y es el mismo usuario, lo actualizamos
        return if (updated.id == id) {
            Ok(repository.update(id, entity.copy(updatedAt = LocalDateTime.now()))!!)
        } else {
            // si no, es que ya existe
            Err(UserError.BadRequest("Ya existe un usuario con username: ${entity.username}"))
        }
    }

    override suspend fun delete(id: UUID): User? {
        logger.debug { "delete: Borrando usuario con id: $id" }

        val user = repository.findById(id)
        user?.let {
            repository.delete(it)
        }
        return user
    }

    override suspend fun isAdmin(id: UUID): Result<Boolean, UserError> {
        logger.debug { "isAdmin: Comprobando si el usuario con id: $id es administrador" }
        return findById(id).andThen {
            Ok(it.role == User.Role.ADMIN)
        }
    }
}