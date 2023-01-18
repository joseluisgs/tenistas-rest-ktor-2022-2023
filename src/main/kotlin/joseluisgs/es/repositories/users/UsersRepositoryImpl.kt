package joseluisgs.es.repositories.users

import joseluisgs.es.db.getUsuariosInit
import joseluisgs.es.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Single
import org.mindrot.jbcrypt.BCrypt
import java.util.*

private val logger = KotlinLogging.logger {}
private const val BCRYPT_SALT = 12

@Single
class UsersRepositoryImpl : UsersRepository {

    // Fuente de datos
    private val usuarios: MutableMap<UUID, User> = mutableMapOf()

    init {
        logger.debug { "Inicializando el repositorio de Usuarios" }

        getUsuariosInit().forEach {
            usuarios[it.id] = it
        }
    }


    override suspend fun findAll(limit: Int?): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los usuarios" }

        return@withContext usuarios.values.take(limit ?: Int.MAX_VALUE).asFlow()
    }

    override suspend fun findAll(): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los usuarios" }

        return@withContext usuarios.values.asFlow()
    }


    override fun hashedPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_SALT))

    override suspend fun checkUserNameAndPassword(username: String, password: String): User? =
        withContext(Dispatchers.IO) {
            val user = findByUsername(username)
            return@withContext user?.let {
                if (BCrypt.checkpw(password, user.password)) {
                    return@withContext user
                }
                return@withContext null
            }
        }

    override suspend fun findById(id: UUID): User? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando usuario con id: $id" }

        return@withContext usuarios[id]
    }

    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        logger.debug { "findByUsername: Buscando usuario con username: $username" }

        return@withContext usuarios.values.find { it.username.lowercase().contains(username.lowercase()) }
    }


    override suspend fun save(entity: User): User = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando usuario: $entity" }

        usuarios[entity.id] = entity
        return@withContext entity
    }

    override suspend fun update(id: UUID, entity: User): User = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando usuario: $entity" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Actualizamos los datos
        val usuarioUpdate = entity.copy(
            nombre = entity.nombre,
            email = entity.email,
            password = entity.password,
            avatar = entity.avatar,
            role = entity.role,
            createdAt = entity.createdAt,
        )
        usuarios[id] = usuarioUpdate
        return@withContext usuarioUpdate
    }

    override suspend fun delete(id: UUID): User? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Eliminando usuario con id: $id" }

        return@withContext usuarios.remove(id)
    }
}