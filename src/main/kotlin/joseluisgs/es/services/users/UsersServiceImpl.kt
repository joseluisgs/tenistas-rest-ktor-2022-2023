package joseluisgs.es.services.users

import joseluisgs.es.exceptions.UserBadRequestException
import joseluisgs.es.exceptions.UserNotFoundException
import joseluisgs.es.exceptions.UserUnauthorizedException
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

    override suspend fun findById(id: UUID): User {
        logger.debug { "findById: Buscando usuario con id: $id" }

        return repository.findById(id) ?: throw UserNotFoundException("No se ha encontrado el usuario con id: $id")
    }

    override suspend fun findByUsername(username: String): User {
        logger.debug { "findByUsername: Buscando usuario con username: $username" }

        return repository.findByUsername(username)
            ?: throw UserNotFoundException("No se ha encontrado el usuario con username: $username")
    }

    override fun hashedPassword(password: String): String {
        logger.debug { "hashedPassword: Hasheando la contraseña" }

        return repository.hashedPassword(password)
    }

    override suspend fun checkUserNameAndPassword(username: String, password: String): User {
        logger.debug { "checkUserNameAndPassword: Comprobando el usuario y contraseña" }

        return repository.checkUserNameAndPassword(username, password)
            ?: throw UserUnauthorizedException("Nombre de usuario o contraseña incorrectos")
    }

    override suspend fun save(entity: User): User {
        logger.debug { "save: Creando usuario" }

        // Sus credenciales son validas y su nombre de usuario no existe
        val existingUser = repository.findByUsername(entity.username)
        if (existingUser != null) {
            throw UserBadRequestException("Ya existe un usuario con username: ${entity.username}")
        }

        val user =
            entity.copy(
                id = UUID.randomUUID(),
                password = hashedPassword(entity.password),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

        return repository.save(user)
    }

    override suspend fun update(id: UUID, entity: User): User {
        logger.debug { "update: Actualizando usuario con id: $id" }

        // No lo necesitamos, pero lo dejamos por si acaso
        val existingUser = repository.findByUsername(entity.username)
        if (existingUser != null && existingUser.id != id) {
            throw UserBadRequestException("Ya existe un usuario con username: ${entity.username}")
        }

        val user =
            entity.copy(
                updatedAt = LocalDateTime.now(),
            )

        return repository.update(id, user)!!

    }

    override suspend fun delete(id: UUID): User? {
        logger.debug { "delete: Borrando usuario con id: $id" }

        // No lo necesitamos, pero lo dejamos por si acaso
        TODO()
    }
}