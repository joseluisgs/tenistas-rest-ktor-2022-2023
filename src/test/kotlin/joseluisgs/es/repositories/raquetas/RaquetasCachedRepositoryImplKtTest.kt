package joseluisgs.es.repositories.raquetas

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.models.Raqueta
import joseluisgs.es.services.cache.raquetas.RaquetasCacheImpl
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
class RaquetasCachedRepositoryImplKtTest {

    val raqueta = Raqueta(
        id = UUID.fromString("044e6ec7-aa6c-46bb-9433-8094ef4ae8bc"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    )

    @MockK
    lateinit var repo: RaquetasRepositoryImpl

    @SpyK
    var cache = RaquetasCacheImpl()

    @InjectMockKs
    lateinit var repository: RaquetasCachedRepositoryImpl

    init {
        MockKAnnotations.init(this)
    }


    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAll() } returns flowOf(raqueta)

        // Llamamos al método
        val result = repository.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repo.findAll() }

    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAllPageable(0, 10) } returns flowOf(raqueta)

        // Llamamos al método
        val result = repository.findAllPageable(0, 10).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
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
        coEvery { repo.findById(any()) } returns raqueta

        // Llamamos al método
        val result = repository.findById(raqueta.id)

        assertAll(
            { assertEquals(raqueta.marca, result!!.marca) },
            { assertEquals(raqueta.precio, result!!.precio) },
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
        coEvery { repo.save(any()) } returns raqueta

        val result = repository.save(raqueta)

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify(exactly = 1) { repo.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repo.findById(any()) } returns raqueta
        coEvery { repo.update(any(), any()) } returns raqueta

        val result = repository.update(raqueta.id, raqueta)!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repo.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { repo.findById(any()) } returns raqueta
        coEvery { repo.delete(any()) } returns raqueta

        val result = repository.delete(raqueta)!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repo.delete(any()) }
    }
}