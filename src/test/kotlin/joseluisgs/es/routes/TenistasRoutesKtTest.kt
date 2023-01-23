package joseluisgs.es.routes

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import joseluisgs.es.dto.TenistaCreateDto
import joseluisgs.es.dto.TenistaDto
import joseluisgs.es.models.Tenista
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.util.*

private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TenistasRoutesKtTest {
    // Cargamos la configuración del entorno
    private val config = ApplicationConfig("application.conf")

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

    val create = TenistaCreateDto(
        nombre = tenista.nombre,
        ranking = tenista.ranking,
        fechaNacimiento = tenista.fechaNacimiento,
        añoProfesional = tenista.añoProfesional,
        altura = tenista.altura,
        peso = tenista.peso,
        manoDominante = tenista.manoDominante,
        tipoReves = tenista.tipoReves,
        puntos = tenista.puntos,
        pais = tenista.pais,
        raquetaId = tenista.raquetaId
    )

    // Esto es muy similar a hacerlo con Postman
    @Test
    @Order(1)
    fun testGetAll() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        val response = client.get("/api/tenistas")

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
        val response = client.get("/api/tenistas?page=1&perPage=10")

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
        val response = client.post("/api/tenistas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        val dto = json.decodeFromString<TenistaDto>(result)
        assertAll(
            { assertEquals(create.nombre, dto.nombre) },
            { assertEquals(create.ranking, dto.ranking) }
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
        var response = client.post("/api/tenistas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        var dto = json.decodeFromString<TenistaDto>(response.bodyAsText())

        response = client.put("/api/tenistas/${dto.id}") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.bodyAsText()

        // Podemos comprobar que el resultado es un JSON analizar la cadena o deserializarlo
        dto = json.decodeFromString<TenistaDto>(result)
        assertAll(
            { assertEquals(create.nombre, dto.nombre) },
            { assertEquals(create.ranking, dto.ranking) }
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
        val response = client.put("/api/tenista/${UUID.randomUUID()}") {
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
        var response = client.post("/api/tenistas") {
            contentType(ContentType.Application.Json)
            setBody(create)
        }

        // cogemos el id del resultado
        val dto = json.decodeFromString<TenistaDto>(response.bodyAsText())

        response = client.delete("/api/tenistas/${dto.id}")

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
        val response = client.delete("/api/tenistas/${UUID.randomUUID()}")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

}