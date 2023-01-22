package joseluisgs.es.repositories.users

import io.ktor.http.*
import io.ktor.server.config.*
import joseluisgs.es.models.User
import joseluisgs.es.repositories.utils.getDataBaseService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersRepositoryImplTest {
    val dataBaseService = getDataBaseService()

    var repository = UsersRepositoryImpl(dataBaseService)

    val user = User(
        id = UUID.fromString("1314a16c-f4f2-47e2-a7e9-21cac686ee55"),
        nombre = "Test",
        username = "test",
        email = "test@test.com",
        password = BCrypt.hashpw("test1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.USER
    )

    @BeforeEach
    fun setUp() {
        dataBaseService.clearDataBaseData()
        dataBaseService.initDataBaseData()
    }

    @AfterAll
    fun tearDown() {
        dataBaseService.clearDataBaseData()
    }


    @Test
    fun findAll() = runTest {
        val result = repository.findAll().take(1).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )
    }

    @Test
    fun testFindAllLimit() = runTest {
        val result = repository.findAll(2).toList()

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals("Pepe Perez", result[0].nombre) },
            { assertEquals("Ana Lopez", result[1].nombre) },
        )
    }

    @Test
    fun checkUserNameAndPassword() = runTest {
        val result = repository.checkUserNameAndPassword("pepe", "pepe1234")

        assertAll(
            { assertEquals("Pepe Perez", result?.nombre) },
            { assertEquals("pepe", result?.username) },
        )
    }

    @Test
    fun checkUserNameAndPasswordNotFound() = runTest {
        val result = repository.checkUserNameAndPassword("caca", "caca1234")

        assertNull(result)
    }

    @Test
    fun findById() = runTest {
        val result = repository.findById("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf".toUUID())

        assertAll(
            { assertEquals("Pepe Perez", result?.nombre) },
            { assertEquals("pepe", result?.username) },
        )
    }

    @Test
    fun findByIdNotFound() = runTest {
        val result = repository.findById(UUID.randomUUID())

        assertNull(result)
    }

    @Test
    fun findByUsername() = runTest {
        val result = repository.findByUsername("pepe")

        assertAll(
            { assertEquals("Pepe Perez", result?.nombre) },
            { assertEquals("pepe", result?.username) },
        )
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        val result = repository.findByUsername("caca")

        assertNull(result)
    }

    @Test
    fun save() = runTest {
        val res = repository.save(user)

        assertAll(
            { assertEquals(user.nombre, res.nombre) },
            { assertEquals(user.username, res.username) },
            { assertEquals(user.email, res.email) },
        )
    }


    @Test
    fun update() = runTest {
        val res = repository.save(user)
        val res2 = repository.update(user.id, res.copy(nombre = "Test2"))!!

        assertAll(
            { assertEquals("Test2", res2.nombre) },
            { assertEquals(user.username, res2.username) },
            { assertEquals(user.email, res2.email) },
        )
    }

    @Test
    fun updateNotFound() = runTest {
        val res = repository.update(UUID.randomUUID(), user)
        assertNull(res)
    }

    @Test
    fun delete() = runTest {
        val res = repository.save(user)
        val res2 = repository.delete(res)!!

        assertAll(
            { assertEquals(user.nombre, res2.nombre) },
            { assertEquals(user.username, res2.username) },
            { assertEquals(user.email, res2.email) },
        )
    }

    @Test
    fun deleteNotFound() = runTest {
        val res = repository.delete(user)

        assertNull(res)
    }
}