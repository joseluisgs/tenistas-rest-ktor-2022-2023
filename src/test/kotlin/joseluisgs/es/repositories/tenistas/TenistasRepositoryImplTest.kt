package joseluisgs.es.repositories.tenistas

import joseluisgs.es.models.Tenista
import joseluisgs.es.repositories.utils.getDataBaseService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepresentantesRepositoryImplKtTest {

    val dataBaseService = getDataBaseService()

    var repository = TenistasRepositoryImpl(dataBaseService)

    val tenista = Tenista(
        id = UUID.fromString("5d1e6fe1-5fa6-4494-a492-ae9725959035"),
        nombre = "Test",
        ranking = 99,
        fechaNacimiento = LocalDate.parse("1981-01-01"),
        añoProfesional = 2000,
        altura = 188,
        peso = 83,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.UNA_MANO,
        puntos = 3789,
        pais = "Suiza",
        raquetaId = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e")
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
            { assertEquals("Carlos Alcaraz", result[0].nombre) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllPageable(0, 10).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Carlos Alcaraz", result[0].nombre) },
        )

    }

    @Test
    fun findById() = runTest {
        val result = repository.findById("ea2962c6-2142-41b8-8dfb-0ecfe67e27df".toUUID())

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals("Rafael Nadal", result?.nombre) },
            { assertEquals(2, result?.ranking) }
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
        val result = repository.findByNombre("Rafael Nadal").take(1).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Rafael Nadal", result[0].nombre) },
            { assertEquals(2, result[0].ranking) }
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
    fun findByRanking() = runTest {
        val result = repository.findByRanking(2)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Rafael Nadal", result?.nombre) },
        )
    }

    @Test
    fun findByRankingNotFound() = runTest {
        val result = repository.findByRanking(999)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }

    @Test
    fun save() = runTest {
        val result = repository.save(tenista)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result.nombre, tenista.nombre) },
            { assertEquals(result.ranking, tenista.ranking) }
        )
    }

    @Test
    fun update() = runTest {
        val res = repository.save(tenista)
        val update = res.copy(nombre = "Test2")
        val result = repository.update(tenista.id, update)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.nombre, update.nombre) },
            { assertEquals(result?.ranking, update.ranking) }
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
        val res = repository.save(tenista)
        val result = repository.delete(res)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals(result?.nombre, res.nombre) },
            { assertEquals(result?.ranking, res.ranking) }
        )
    }

    @Test
    fun deleteNotExists() = runTest {
        val delete = tenista.copy(id = UUID.randomUUID())
        val result = repository.delete(delete)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }

}