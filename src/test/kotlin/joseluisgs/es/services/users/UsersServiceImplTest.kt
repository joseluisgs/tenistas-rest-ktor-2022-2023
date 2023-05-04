package joseluisgs.es.services.users

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.exceptions.UserException
import joseluisgs.es.models.User
import joseluisgs.es.repositories.users.UsersRepositoryImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class UsersServiceImplTest {

    val user = User(
        id = UUID.fromString("1314a16c-f4f2-47e2-a7e9-21cac686ee55"),
        nombre = "Test",
        username = "test",
        email = "test@test.com",
        password = BCrypt.hashpw("test1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.USER
    )

    @MockK
    lateinit var repository: UsersRepositoryImpl

    @InjectMockKs
    lateinit var service: UsersServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        coEvery { repository.findAll(any()) } returns flowOf(user)

        val result = service.findAll(null)
        val expected = result.take(1).toList()

        assertAll(
            { assertEquals(1, expected.size) },
            { assertEquals("Test", expected[0].nombre) },
        )

        coVerify { repository.findAll(any()) }

    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns user

        val result = service.findById(user.id)

        assertAll(
            { assertEquals("Test", result.nombre) },
            { assertEquals("test", result.username) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = assertThrows<UserException.NotFound> {
            service.findById(user.id)
        }

        assertEquals("No se ha encontrado el usuario con id: ${user.id}", res.message)

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByUsername() = runTest {
        coEvery { repository.findByUsername(any()) } returns user

        val result = service.findByUsername(user.username)

        assertAll(
            { assertEquals("Test", result.nombre) },
            { assertEquals("test", result.username) },
        )

        coVerify { repository.findByUsername(any()) }
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        coEvery { repository.findByUsername(any()) } returns null

        val res = assertThrows<UserException.NotFound> {
            service.findByUsername(user.username)
        }

        assertEquals("No se ha encontrado el usuario con username: ${user.username}", res.message)

        coVerify { repository.findByUsername(any()) }
    }

    @Test
    fun hashedPassword() = runTest {
        coEvery { repository.hashedPassword(any()) } returns BCrypt.hashpw("test1234", BCrypt.gensalt(12))

        val result = service.hashedPassword("test1234")

        assertTrue(BCrypt.checkpw("test1234", result))

        coVerify { repository.hashedPassword(any()) }
    }

    @Test
    fun checkUserNameAndPassword() = runTest {
        coEvery { repository.checkUserNameAndPassword(any(), any()) } returns user

        val result = service.checkUserNameAndPassword(user.username, "test1234")

        assertAll(
            { assertEquals("Test", result.nombre) },
            { assertEquals("test", result.username) },
        )

        coVerify { repository.checkUserNameAndPassword(any(), any()) }
    }

    @Test
    fun checkUserNameAndPasswordNotFound() = runTest {
        coEvery { repository.checkUserNameAndPassword(any(), any()) } returns null

        val res = assertThrows<UserException.NotFound> {
            service.checkUserNameAndPassword(user.username, "test1234")
        }

        assertEquals("Nombre de usuario o contraseña incorrectos", res.message)
    }

    @Test
    fun save() = runTest {
        coEvery { repository.save(any()) } returns user

        val result = service.save(user)

        assertAll(
            { assertEquals("Test", result.nombre) },
            { assertEquals("test", result.username) },
        )

        coVerify { repository.save(any()) }
    }

    @Test
    fun update() = runTest {
        coEvery { repository.update(any(), any()) } returns user

        val result = service.update(user.id, user)

        assertAll(
            { assertEquals("Test", result.nombre) },
            { assertEquals("test", result.username) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findById(any()) } returns user
        coEvery { repository.delete(any()) } returns user

        service.delete(user.id)

        coVerify { repository.delete(any()) }
    }
}