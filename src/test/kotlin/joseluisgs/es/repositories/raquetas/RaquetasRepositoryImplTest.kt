package joseluisgs.es.repositories.raquetas

import joseluisgs.es.models.Raqueta
import joseluisgs.es.repositories.utils.getDataBaseService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RaquetasRepositoryImplKtTest {

    val dataBaseService = getDataBaseService()

    var repository = RaquetasRepositoryImpl(dataBaseService)

    val raqueta = Raqueta(
        id = UUID.fromString("044e6ec7-aa6c-46bb-9433-8094ef4ae8bc"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
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
        val result = repository.findAll().toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Babolat", result[0].marca) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllPageable(0, 10).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Babolat", result[0].marca) },
        )

    }

    @Test
    fun findById() = runTest {
        val result = repository.findById("86084458-4733-4d71-a3db-34b50cd8d68f".toUUID())

        // Comprobamos que el resultado es correcto
        Assertions.assertAll(
            { assertEquals("Babolat", result?.marca) },
            { assertEquals(200.0, result?.precio) },
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
        val result = repository.findByMarca("Babolat").take(1).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(1, result.size) },
            { assertEquals("Babolat", result[0].marca) },
        )
    }

    @Test
    fun findByUsernameNotFound() = runTest {
        val result = repository.findByMarca("caca").take(1).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(0, result.size) },
        )
    }

    @Test
    fun save() = runTest {
        val result = repository.save(raqueta)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result.marca, raqueta.marca) },
            { assertEquals(result.precio, raqueta.precio) }
        )
    }

    @Test
    fun update() = runTest {
        val res = repository.save(raqueta)
        val update = res.copy(marca = "Test2")
        val result = repository.update(raqueta.id, update)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.marca, update.marca) },
            { assertEquals(result?.precio, update.precio) }
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
        val res = repository.save(raqueta)
        val result = repository.delete(res)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.marca, res.marca) },
            { assertEquals(result?.precio, res.precio) }
        )
    }

    @Test
    fun deleteNotExists() = runTest {
        val delete = raqueta.copy(id = UUID.randomUUID())
        val result = repository.delete(delete)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }

}