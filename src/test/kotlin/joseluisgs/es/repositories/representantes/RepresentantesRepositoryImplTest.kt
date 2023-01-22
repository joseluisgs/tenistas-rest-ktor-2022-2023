package joseluisgs.es.repositories.representantes

import joseluisgs.es.models.Representante
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepresentantesRepositoryImplKtTest {

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )

    val repository: RepresentantesRepository = RepresentantesRepositoryImpl()

    // Con run test podemos ejecutar código asíncrono
    @BeforeEach
    fun setUp() = runTest {
        repository.save(representante)
    }

    @AfterEach
    fun tearDwon() = runTest {
        repository.delete(representante)
    }

    @Test
    fun findAll() = runTest {
        val result = repository.findAll()
        val representantes = mutableListOf<Representante>()
        result.collect { representantes.add(it) }

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertTrue(representantes.size > 0) },
            { assertTrue(representantes.contains(representante)) }
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllPageable(0, 10)
        val representantes = mutableListOf<Representante>()
        result.collect { representantes.add(it) }

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertTrue(representantes.size > 0) },
            { assertTrue(representantes.contains(representante)) }
        )

    }

    @Test
    fun findById() = runTest {
        val result = repository.findById(representante.id)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals(representante, result) }
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
        val result = repository.findByNombre("Test")
        val representantes = mutableListOf<Representante>()
        result.collect { representantes.add(it) }

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertTrue(representantes.size > 0) },
            { assertTrue(representantes.contains(representante)) }
        )
    }

    @Test
    fun save() = runTest {
        val result = repository.save(representante)

        // Comprobamos que el resultado es correcto
        assertEquals(representante, result)
    }

    @Test
    fun update() = runTest {
        val update = representante.copy(nombre = "Test2")
        val result = repository.update(representante.id, update)

        // Comprobamos que el resultado es correcto
        assertEquals(update, result)
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
        val result = repository.delete(representante)

        // Comprobamos que el resultado es correcto
        assertEquals(representante, result)
    }

    @Test
    fun deleteNotExists() = runTest {
        val delete = representante.copy(id = UUID.randomUUID())
        val result = repository.delete(delete)

        // Comprobamos que el resultado es correcto
        assertNull(result)
    }

}