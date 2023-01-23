package joseluisgs.es.repositories.tenistas

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.models.Tenista
import joseluisgs.es.services.cache.tenistas.TenistasCacheImpl
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class TenistasCachedRepositoryImplKtTest {

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

    @MockK
    lateinit var repo: TenistasRepositoryImpl

    @SpyK
    var cache = TenistasCacheImpl()

    @InjectMockKs
    lateinit var repository: TenistasCachedRepositoryImpl

    init {
        MockKAnnotations.init(this)
    }


    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAll() } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repo.findAll() }

    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAllPageable(0, 10) } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findAllPageable(0, 10).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
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
        coEvery { repo.findById(any()) } returns tenista

        // Llamamos al método
        val result = repository.findById(tenista.id)

        assertAll(
            { assertEquals(tenista.nombre, result!!.nombre) },
            { assertEquals(tenista.ranking, result!!.ranking) },
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
    fun findByRanking() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByRanking(any()) } returns tenista

        // Llamamos al método
        val result = repository.findByRanking(99)

        assertAll(
            { assertEquals(tenista.nombre, result!!.nombre) },
            { assertEquals(tenista.ranking, result!!.ranking) },
        )

        coVerify(exactly = 1) { repo.findByRanking(any()) }
    }

    @Test
    fun findByNombre() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByNombre(any()) } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findByNombre("Test").toList()

        assertAll(
            { assertEquals(tenista.nombre, result[0].nombre) },
            { assertEquals(tenista.ranking, result[0].ranking) },
        )

        coVerify(exactly = 1) { repo.findByNombre(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { repo.save(any()) } returns tenista

        val result = repository.save(tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify(exactly = 1) { repo.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repo.findById(any()) } returns tenista
        coEvery { repo.update(any(), any()) } returns tenista

        val result = repository.update(tenista.id, tenista)!!

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repo.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { repo.findById(any()) } returns tenista
        coEvery { repo.delete(any()) } returns tenista

        val result = repository.delete(tenista)!!

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repo.delete(any()) }
    }
}