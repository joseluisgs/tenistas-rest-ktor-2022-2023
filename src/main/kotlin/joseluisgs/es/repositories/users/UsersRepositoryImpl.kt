package joseluisgs.es.repositories.users

import joseluisgs.es.entities.UsersTable
import joseluisgs.es.mappers.toEntity
import joseluisgs.es.mappers.toModel
import joseluisgs.es.models.User
import joseluisgs.es.services.database.DataBaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Single
import org.mindrot.jbcrypt.BCrypt
import java.util.*

private val logger = KotlinLogging.logger {}
private const val BCRYPT_SALT = 12

@Single
class UsersRepositoryImpl(
    private val dataBaseService: DataBaseService
) : UsersRepository {

    init {
        logger.debug { "Inicializando el repositorio de Usuarios" }
    }

    override suspend fun findAll(limit: Int?): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los usuarios" }

        val myLimit = limit ?: Int.MAX_VALUE

        return@withContext (dataBaseService.client selectFrom UsersTable limit myLimit.toLong())
            .fetchAll()
            .map { it.toModel() }
    }

    override suspend fun findAll(): Flow<User> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los usuarios" }

        return@withContext (dataBaseService.client selectFrom UsersTable)
            .fetchAll()
            .map { it.toModel() }
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

        return@withContext (dataBaseService.client selectFrom UsersTable
                where UsersTable.id eq id
                ).fetchFirstOrNull()?.toModel()
    }

    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        logger.debug { "findByUsername: Buscando usuario con username: $username" }

        return@withContext (dataBaseService.client selectFrom UsersTable
                where UsersTable.username eq username
                ).fetchFirstOrNull()?.toModel()
    }


    override suspend fun save(entity: User): User = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando usuario: $entity" }

        return@withContext (dataBaseService.client insertAndReturn entity.toEntity())
            .toModel()

    }

    override suspend fun update(id: UUID, entity: User): User? = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando usuario: $entity" }

        // Buscamos, viene filtrado y si no el update no hace nada
        // val usuario = findById(id)

        // Actualizamos los datos
        entity.let {
            val updateEntity = entity.toEntity()

            val res = (dataBaseService.client update UsersTable
                    set UsersTable.nombre eq updateEntity.nombre
                    set UsersTable.email eq updateEntity.email
                    set UsersTable.password eq updateEntity.password
                    set UsersTable.avatar eq updateEntity.avatar
                    set UsersTable.role eq updateEntity.role
                    set UsersTable.updatedAt eq updateEntity.updatedAt
                    where UsersTable.id eq id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }

    }

    override suspend fun delete(entity: User): User? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Eliminando usuario con id: ${entity.id}" }

        //val usuario = findById(id)

        entity.let {
            val res = (dataBaseService.client deleteFrom UsersTable
                    where UsersTable.id eq it.id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }
    }
}