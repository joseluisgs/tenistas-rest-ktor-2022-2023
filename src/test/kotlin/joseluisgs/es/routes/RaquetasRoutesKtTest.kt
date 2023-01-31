package joseluisgs.es.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import joseluisgs.es.dto.RaquetaCreateDto
import joseluisgs.es.dto.RaquetaDto
import joseluisgs.es.models.Raqueta
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*


private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class RaquetasRoutesKtTest {
    // Cargamos la configuración del entorno
    private val config = ApplicationConfig("application.conf")

    val raqueta = Raqueta(
        id = UUID.fromString("044e6ec7-aa6c-46bb-9433-8094ef4ae8bc"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    )

    val create = RaquetaCreateDto(
        marca = raqueta.marca,
        precio = raqueta.precio,
        representanteId = raqueta.representanteId
    )

    // Esto es muy similar a hacerlo con Postman
    @Test
    @Order(1)
    fun testGetAll() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        val response = client.get("/api/raquetas")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
        // Tambien podemos comprobar el contenido
        // assertEquals("Hello World!", response.body())
        // val result = response.bodyAsText()
        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        //val list = json.decodeFromString<List<RepresentanteDto>>(result)

    }

    @Test
    @Order(2)
    fun testGetAllPageable() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        val response = client.get("/api/raquetas?page=1&perPage=10")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    @Order(3)
    fun testPost() = testApplication {

        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta
        val response = client.post("/api/raquetas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        val dto = json.decodeFromString<RaquetaDto>(result)
        assertAll(
            { assertEquals(create.marca, dto.marca) },
            { assertEquals(create.precio, dto.precio) }
        )
    }

    @Test
    @Order(4)
    fun testPut() = testApplication {

        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta para crearlo y que este
        var response = client.post("/api/raquetas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        var dto = json.decodeFromString<RaquetaDto>(response.bodyAsText())

        response = client.put("/api/raquetas/${dto.id}") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        dto = json.decodeFromString<RaquetaDto>(result)
        assertAll(
            { assertEquals(create.marca, dto.marca) },
            { assertEquals(create.precio, dto.precio) }
        )
    }

    @Test
    @Order(5)
    fun testPutNotFound() = testApplication {

        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta para crearlo y que este
        val response = client.put("/api/raquetas/${UUID.randomUUID()}") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    @Order(6)
    fun testDelete() = testApplication {
        
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Lanzamos la consulta para crearlo y que este
        var response = client.post("/api/raquetas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        val dto = json.decodeFromString<RaquetaDto>(response.bodyAsText())

        response = client.delete("/api/raquetas/${dto.id}")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    @Order(7)
    fun testDeleteNotFound() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Creamos el cliente, como vamos a enviar JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta para crearlo y que este
        val response = client.delete("/api/raquetas/${UUID.randomUUID()}")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}