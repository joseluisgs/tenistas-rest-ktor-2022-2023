package joseluisgs.es.services.tenistas

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.exceptions.TenistaNotFoundException
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Tenista
import joseluisgs.es.repositories.raquetas.RaquetasRepositoryImpl
import joseluisgs.es.repositories.tenistas.TenistasCachedRepositoryImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class TenistasServiceImplTest {

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

    val raqueta = Raqueta(
        id = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2"),
        marca = "Head",
        precio = 225.0,
        represetanteId = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd")
    )


    @MockK
    lateinit var repository: TenistasCachedRepositoryImpl

    @MockK
    lateinit var raquetasRepository: RaquetasRepositoryImpl

    @InjectMockKs
    lateinit var service: TenistasServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(tenista)

        // Llamamos al método
        val result = service.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findAllPageable() = runTest {
        coEvery { repository.findAllPageable(any()) } returns flowOf(tenista)

        val result = service.findAllPageable(1, 10).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAllPageable(any()) }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns tenista

        val result = service.findById(tenista.id)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = assertThrows<TenistaNotFoundException> {
            service.findById(tenista.id)
        }

        assertEquals("No se ha encontrado el tenista con id: ${tenista.id}", res.message)

        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByNombre() = runTest {
        coEvery { repository.findByNombre(any()) } returns flowOf(tenista)

        val result = service.findByNombre(tenista.nombre).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repository.findByNombre(any()) }
    }

    @Test
    fun findByRanking() = runTest {
        coEvery { repository.findByRanking(any()) } returns tenista

        val result = service.findByRanking(tenista.ranking)

        assertAll(
            { assertEquals(tenista, result) }
        )

        coVerify(exactly = 1) { repository.findByRanking(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { raquetasRepository.findById(any()) } returns raqueta
        coEvery { repository.save(any()) } returns tenista

        val result = service.save(tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repository.findById(any()) } returns tenista
        coEvery { raquetasRepository.findById(any()) } returns raqueta
        coEvery { repository.update(any(), any()) } returns tenista

        val result = service.update(tenista.id, tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findById(any()) } throws TenistaNotFoundException("No se ha encontrado el tenista con id: ${tenista.id}")
        coEvery { repository.update(any(), any()) } returns null

        val res = assertThrows<TenistaNotFoundException> {
            service.update(tenista.id, tenista)
        }

        assertEquals("No se ha encontrado el tenista con id: ${tenista.id}", res.message)

        coVerify(exactly = 0) { repository.update(any(), any()) }

    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findById(any()) } returns tenista
        coEvery { repository.delete(any()) } returns tenista

        val result = service.delete(tenista.id)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.delete(any()) }

    }

    @Test
    fun deleteNotFound() = runTest {
        coEvery { repository.findById(any()) } throws TenistaNotFoundException("No se ha encontrado el tenista con id: ${tenista.id}")

        val res = assertThrows<TenistaNotFoundException> {
            service.delete(UUID.randomUUID())
        }

        assertEquals("No se ha encontrado el tenista con id: ${tenista.id}", res.message)

        coVerify { repository.delete(any()) }
    }

}