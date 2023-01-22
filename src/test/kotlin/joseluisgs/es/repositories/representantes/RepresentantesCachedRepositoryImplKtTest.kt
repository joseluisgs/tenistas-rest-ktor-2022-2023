package joseluisgs.es.repositories.representantes

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.models.Representante
import joseluisgs.es.services.cache.representantes.RepresentantesCacheImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class RepresentantesCachedRepositoryImplKtTest {

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )

    @MockK
    lateinit var repo: RepresentantesRepositoryImpl

    @SpyK
    var cache = RepresentantesCacheImpl()

    @InjectMockKs
    lateinit var repository: RepresentantesCachedRepositoryImpl

    init {
        MockKAnnotations.init(this)
    }


    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAll() } returns flowOf(representante)

        // Llamamos al método
        val result = repository.findAll()
        val representantes = mutableListOf<Representante>()

        result.collect {
            representantes.add(it)
        }

        assertAll(
            { assertEquals(1, representantes.size) },
            { assertEquals(representante, representantes[0]) }
        )

        coVerify(exactly = 1) { repo.findAll() }

    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAllPageable(0, 10) } returns flowOf(representante)

        // Llamamos al método
        val result = repository.findAllPageable(0, 10)
        val representantes = mutableListOf<Representante>()

        result.collect {
            representantes.add(it)
        }

        assertAll(
            { assertEquals(1, representantes.size) },
            { assertEquals(representante, representantes[0]) }
        )

        coVerify(exactly = 1) { repo.findAllPageable(0, 10) }
    }


    /* @Test
     fun findByNombre() = runTest {
         // Usamos coEvery para poder usar corutinas
         coEvery { repo.findByNombre(any()) } returns flowOf(representante)


         // Llamamos al método
         val result = repository.findByNombre("Test")
         val representantes = mutableListOf<Representante>()

         result.collect {
             representantes.add(it)
         }

         assertAll(
             { assertEquals(1, representantes.size) },
             { assertEquals(representante, representantes[0]) }
         )

         coVerify { repo.findByNombre(any()) }
     }*/

    @Test
    fun findById() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns representante

        // Llamamos al método
        val result = repository.findById(representante.id)

        assertAll(
            { assertEquals(representante.nombre, result!!.nombre) },
            { assertEquals(representante.email, result!!.email) },
        )


        coVerify { repo.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns null

        // Llamamos al método
        val result = repository.findById(UUID.randomUUID())

        assertNull(result)

        coVerify { repo.findById(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { repo.save(any()) } returns representante

        val result = repository.save(representante)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify(exactly = 1) { repo.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repo.findById(any()) } returns representante
        coEvery { repo.update(any(), any()) } returns representante

        val result = repository.update(representante.id, representante)!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repo.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { repo.findById(any()) } returns representante
        coEvery { repo.delete(any()) } returns representante

        val result = repository.delete(representante)!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repo.delete(any()) }
    }
}