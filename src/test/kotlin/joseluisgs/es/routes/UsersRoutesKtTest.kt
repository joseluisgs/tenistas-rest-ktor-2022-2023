package joseluisgs.es.routes

import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserDto
import joseluisgs.es.dto.UserLoginDto
import joseluisgs.es.dto.UserWithTokenDto
import joseluisgs.es.models.User
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.*


private val json = Json { ignoreUnknownKeys = true }

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UsersRoutesKtTest {
    // Cargamos la configuración del entorno
    private val config = ApplicationConfig("application.conf")

    val userDto = UserCreateDto(
        nombre = "Test",
        email = "test@test.com",
        username = "test",
        password = "test12345",
        avatar = "https://www.google.com/avatar.png",
        role = User.Role.USER
    )

    val loginDto = UserLoginDto(
        username = userDto.username,
        password = userDto.password,
    )

    @Test
    @Order(1)
    fun registerUserTest() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta
        val response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(response.status, HttpStatusCode.Created)
        // Tambien podemos comprobar el contenido
        val res = json.decodeFromString<UserDto>(response.bodyAsText())
        assertAll(
            { assertEquals(res.nombre, userDto.nombre) },
            { assertEquals(res.email, userDto.email) },
            { assertEquals(res.username, userDto.username) },
            { assertEquals(res.avatar, userDto.avatar) },
            { assertEquals(res.role, userDto.role) },
        )
    }

    @Test
    @Order(2)
    fun login() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Lanzamos la consulta
        val response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }


        // Lanzamos la consulta
        val responseLogin = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(responseLogin.status, HttpStatusCode.OK)
        // Tambien podemos comprobar el contenido
        val res = json.decodeFromString<UserWithTokenDto>(responseLogin.bodyAsText())
        assertAll(
            { assertEquals(res.user.nombre, userDto.nombre) },
            { assertEquals(res.user.email, userDto.email) },
            { assertEquals(res.user.username, userDto.username) },
            { assertEquals(res.user.avatar, userDto.avatar) },
            { assertEquals(res.user.role, userDto.role) },
            { assertNotNull(res.token) },
        )
    }

    @Test
    @Order(3)
    fun meInfoTest() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        // Lanzamos la consulta
        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(response.status, HttpStatusCode.OK)
        // Tambien podemos comprobar el contenido

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        // tomamos el token
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from a local storage and return them as the 'BearerTokens' instance
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        // Lanzamos la consulta
        response = client.get("/api/users/me") {
            contentType(ContentType.Application.Json)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(response.status, HttpStatusCode.OK)
        // Tambien podemos comprobar el contenido
        val resUser = json.decodeFromString<UserDto>(response.bodyAsText())
        assertAll(
            { assertEquals(resUser.nombre, userDto.nombre) },
            { assertEquals(resUser.email, userDto.email) },
            { assertEquals(resUser.username, userDto.username) },
            { assertEquals(resUser.avatar, userDto.avatar) },
            { assertEquals(resUser.role, userDto.role) },
        )
    }

    @Test
    fun testUpload() = testApplication {

        environment { config }

        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        var response = client.post("/api/users/register") {
            contentType(ContentType.Application.Json)
            setBody(userDto)
        }

        // Lanzamos la consulta
        response = client.post("/api/users/login") {
            contentType(ContentType.Application.Json)
            setBody(loginDto)
        }

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(response.status, HttpStatusCode.OK)
        // Tambien podemos comprobar el contenido

        val res = json.decodeFromString<UserWithTokenDto>(response.bodyAsText())
        // tomamos el token
        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        // Load tokens from a local storage and return them as the 'BearerTokens' instance
                        BearerTokens(res.token, res.token)
                    }
                }
            }
        }

        // Lanzamos la consulta
        val boundary = "WebAppBoundary"
        response = client.patch("/api/users/me") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // Cojo una imagen de un directorio resources y la envío
                        val image = File("./images/ktor.png")
                        append("file", image.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(HttpHeaders.ContentDisposition, "filename=\"ktor.png\"")
                        })
                    },
                    boundary,
                    ContentType.MultiPart.FormData.withParameter("boundary", boundary)
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        // Puedes ver lo que sale!!!
        println(response.bodyAsText())
    }

    // Como ves estamos haciendo como con Postman, pero en código
    // Puedes ver más info https://ktor.io/docs/request.html
}