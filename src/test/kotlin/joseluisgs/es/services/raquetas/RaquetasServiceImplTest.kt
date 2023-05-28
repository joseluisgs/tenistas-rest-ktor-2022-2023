package joseluisgs.es.services.raquetas

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.errors.RaquetaError
import joseluisgs.es.exceptions.DataBaseIntegrityViolationException
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.raquetas.RaquetasCachedRepositoryImpl
import joseluisgs.es.repositories.representantes.RepresentantesCachedRepositoryImpl
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class RaquetasServiceImplTest {

    val raqueta = Raqueta(
        id = UUID.fromString("044e6ec7-aa6c-46bb-9433-8094ef4ae8bc"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    )

    val representante = Representante(
        id = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        email = "pepe@perez.com"
    )


    @MockK
    lateinit var repository: RaquetasCachedRepositoryImpl

    @MockK
    lateinit var represetantesRepository: RepresentantesCachedRepositoryImpl

    @InjectMockKs
    lateinit var service: RaquetasServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(raqueta)

        // Llamamos al método
        val result = service.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findAllPageable() = runTest {
        coEvery { repository.findAllPageable(any()) } returns flowOf(raqueta)

        val result = service.findAllPageable(1, 10).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAllPageable(any()) }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns raqueta

        val result = service.findById(raqueta.id).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = service.findById(raqueta.id).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertEquals("No se ha encontrado la raqueta con id: ${raqueta.id}", res.message) },
        )

        assertEquals("No se ha encontrado la raqueta con id: ${raqueta.id}", res.message)

        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByMarca() = runTest {
        coEvery { repository.findByMarca(any()) } returns flowOf(raqueta)

        val result = service.findByMarca(raqueta.marca).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repository.findByMarca(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { represetantesRepository.findById(any()) } returns representante
        coEvery { repository.save(any()) } returns raqueta

        val result = service.save(raqueta).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.save(any()) }
    }

    @Test
    fun saveRepresentanteNotExists() = runTest {
        coEvery { represetantesRepository.findById(any()) } returns null
        coEvery { repository.save(any()) } returns raqueta

        val res = service.save(raqueta).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.RepresentanteNotFound) },
            { assertEquals("No se ha encontrado el representante con id: ${raqueta.representanteId}", res.message) },
        )

        coVerify { repository.save(any()) }
    }

    @Test
    fun update() = runTest {
        coEvery { represetantesRepository.findById(any()) } returns representante
        coEvery { repository.findById(any()) } returns raqueta
        coEvery { repository.update(any(), any()) } returns raqueta

        val result = service.update(raqueta.id, raqueta).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null
        coEvery { repository.update(any(), any()) } returns null

        val res = service.update(raqueta.id, raqueta).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertEquals("No se ha encontrado la raqueta con id: ${raqueta.id}", res.message) },
        )

        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun updateRepresentanteNotExists() = runTest {
        coEvery { represetantesRepository.findById(any()) } returns null
        coEvery { repository.update(any(), any()) } returns raqueta

        val res = service.update(raqueta.id, raqueta).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.RepresentanteNotFound) },
            { assertEquals("No se ha encontrado el representante con id: ${raqueta.representanteId}", res.message) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findById(any()) } returns raqueta
        coEvery { repository.delete(any()) } returns raqueta
        coEvery { represetantesRepository.findById(any()) } returns representante

        val result = service.delete(raqueta.id).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.delete(any()) }
    }

    @Test
    fun deleteNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = service.delete(raqueta.id).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertEquals("No se ha encontrado la raqueta con id: ${raqueta.id}", res.message) },
        )

        coVerify { repository.delete(any()) }
    }

    @Test
    fun deleteNotRaquetaConflict() = runTest {
        val uuid = "86084458-4733-4d71-a3db-34b50cd8d68f".toUUID()
        coEvery { repository.findById(any()) } returns raqueta
        coEvery { repository.delete(any()) } throws DataBaseIntegrityViolationException()

        val res = service.delete(uuid).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.ConflictIntegrity) },
            {
                assertEquals(
                    "No se puede borrar la raqueta con id: $uuid porque tiene tenistas asociados",
                    res.message
                )
            },
        )

        coVerify { repository.delete(any()) }
    }
}