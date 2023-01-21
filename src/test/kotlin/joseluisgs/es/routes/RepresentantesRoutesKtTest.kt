package joseluisgs.es.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.mappers.toDto
import joseluisgs.es.models.Representante
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*


private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)

class RepresentantesRoutesKtTest {
    // Cargamos la configuración del entorno
    private val config = ApplicationConfig("application.conf")

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )

    // Esto es muy similar a hacerlo con Postman
    @Test
    fun testGetAll() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        //val response = client.get("/api/representantes")

        // Comprobamos que la respuesta y el contenido es correcto
        // assertEquals(HttpStatusCode.OK, response.status)
        // Tambien podemos comprobar el contenido
        // assertEquals("Hello World!", response.body())
        // val result = response.bodyAsText()
        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        //val list = json.decodeFromString<List<RepresentanteDto>>(result)

    }

    @Test
    fun testGetAllPageable() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        val response = client.get("/api/representantes?page=1&perPage=10")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testPost() = testApplication {
        val create = representante.toDto()
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta
        val response = client.post("/api/representantes") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        val dto = json.decodeFromString<RepresentanteDto>(result)
        assertAll(
            { assertEquals(create.nombre, dto.nombre) },
            { assertEquals(create.email, dto.email) }
        )
    }

    @Test
    fun testPut() = testApplication {
        val create = representante.toDto()
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta para crearlo y que este
        var response = client.post("/api/representantes") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        var dto = json.decodeFromString<RepresentanteDto>(response.bodyAsText())

        response = client.put("/api/representantes/${dto.id}") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        dto = json.decodeFromString<RepresentanteDto>(result)
        assertAll(
            { assertEquals(create.nombre, dto.nombre) },
            { assertEquals(create.email, dto.email) }
        )
    }

    @Test
    fun testPutNotFound() = testApplication {
        val create = representante.toDto()
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta para crearlo y que este
        val response = client.put("/api/representantes/${UUID.randomUUID()}") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testDelete() = testApplication {
        val create = representante.toDto()
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta para crearlo y que este
        var response = client.post("/api/representantes") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        val dto = json.decodeFromString<RepresentanteDto>(response.bodyAsText())

        response = client.delete("/api/representantes/${dto.id}")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun testDeleteNotFound() = testApplication {
        val create = representante.toDto()
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta para crearlo y que este
        val response = client.delete("/api/representantes/${UUID.randomUUID()}")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}