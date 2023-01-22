package joseluisgs.es.services.representantes

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.models.Representante
import joseluisgs.es.repositories.representantes.RepresentantesCachedRepositoryImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class RepresentantesServiceImplTest {

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )


    @MockK
    lateinit var repository: RepresentantesCachedRepositoryImpl

    @InjectMockKs
    lateinit var service: RepresentantesServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(representante)

        // Llamamos al método
        val result = service.findAll()
        val representantes = mutableListOf<Representante>()

        result.collect {
            representantes.add(it)
        }

        assertAll(
            { assertEquals(1, representantes.size) },
            { assertEquals(representante, representantes[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findAllPageable() = runTest {
        coEvery { repository.findAllPageable(any()) } returns flowOf(representante)

        val result = service.findAllPageable(1, 10)
        val representantes = mutableListOf<Representante>()
        result.collect {
            representantes.add(it)
        }

        assertAll(
            { assertEquals(1, representantes.size) },
            { assertEquals(representante, representantes[0]) }
        )

        coVerify(exactly = 1) { repository.findAllPageable(any()) }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns representante

        val result = service.findById(representante.id)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = assertThrows<RepresentanteNotFoundException> {
            service.findById(representante.id)
        }

        assertEquals("No se ha encontrado el representante con id: ${representante.id}", res.message)

        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByNombre() = runTest {
        coEvery { repository.findByNombre(any()) } returns flowOf(representante)

        val result = service.findByNombre(representante.nombre)
        val representantes = mutableListOf<Representante>()
        result.collect {
            representantes.add(it)
        }

        assertAll(
            { assertEquals(1, representantes.size) },
            { assertEquals(representante, representantes[0]) }
        )

        coVerify(exactly = 1) { repository.findByNombre(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { repository.save(any()) } returns representante

        val result = service.save(representante)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repository.findById(any()) } returns representante
        coEvery { repository.update(any(), any()) } returns representante

        val result = service.update(representante.id, representante)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findById(any()) } throws RepresentanteNotFoundException("No se ha encontrado el representante con id: ${representante.id}")
        coEvery { repository.update(any(), any()) } returns null

        val res = assertThrows<RepresentanteNotFoundException> {
            service.update(representante.id, representante)
        }

        assertEquals("No se ha encontrado el representante con id: ${representante.id}", res.message)

        coVerify(exactly = 0) { repository.update(any(), any()) }

    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findById(any()) } returns representante
        coEvery { repository.delete(any()) } returns representante

        val result = service.delete(representante.id)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.delete(any()) }

    }

    @Test
    fun deleteNotFound() = runTest {
        coEvery { repository.delete(any()) } throws RepresentanteNotFoundException("No se ha encontrado el representante con id: ${representante.id}")

        val res = assertThrows<RepresentanteNotFoundException> {
            service.delete(representante.id)
        }

        assertEquals("No se ha encontrado el representante con id: ${representante.id}", res.message)

        coVerify { repository.delete(any()) }
    }
}