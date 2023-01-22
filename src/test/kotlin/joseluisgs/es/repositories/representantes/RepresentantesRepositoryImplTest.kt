package joseluisgs.es.repositories.representantes

import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.utils.getDataBaseService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepresentantesRepositoryImplKtTest {

    val dataBaseService = getDataBaseService()

    var repository = RepresentantesRepositoryImpl(dataBaseService)

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )


    // Con run test podemos ejecutar código asíncrono
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

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllPageable(0, 10).take(1).toList()
        val representantes = mutableListOf<Representante>()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )

    }

    @Test
    fun findById() = runTest {
        val result = repository.findById("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf".toUUID())

        // Comprobamos que el resultado es correcto
        Assertions.assertAll(
            { assertEquals("Pepe Perez", result?.nombre) },
            { assertEquals("pepe@perez.com", result?.email) },
        )
    }

    @Test
    fun findByIdNotExists() = runTest {
        val result = repository.findById(UUID.randomUUID())

        // Comprobamos que el resultado es correcto
        assertNull(result)

    }

    @Test
    fun findByNombre() = runTest {
        val result = repository.findByNombre("Pepe Perez").take(1).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        val result = repository.findByNombre("caca").take(1).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(0, result.size) },
        )
    }

    @Test
    fun save() = runTest {
        val result = repository.save(representante)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result.nombre, representante.nombre) },
            { assertEquals(result.email, representante.email) }
        )
    }

    @Test
    fun update() = runTest {
        val res = repository.save(representante)
        val update = res.copy(nombre = "Test2")
        val result = repository.update(representante.id, update)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.nombre, update.nombre) },
            { assertEquals(result?.email, update.email) }
        )
    }

    /*
    No hace falta porque filtro por la cache!!
    @Test
    fun updateNotExists() = runTest {
        val update = representante.copy(nombre = "Test2")
        val result = repository.update(UUID.randomUUID(), update)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }*/

    @Test
    fun delete() = runTest {
        val res = repository.save(representante)
        val result = repository.delete(res)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.nombre, res.nombre) },
            { assertEquals(result?.email, res.email) }
        )
    }

    @Test
    fun deleteNotExists() = runTest {
        val delete = representante.copy(id = UUID.randomUUID())
        val result = repository.delete(delete)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }

}