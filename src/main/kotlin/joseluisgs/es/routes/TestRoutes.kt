package joseluisgs.es.routes

import io.github.smiley4.ktorswaggerui.dsl.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.TestDto
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/test" // Ruta de acceso, puede aunar un recurso

/**
 * Rutas de test
 * Estan comentadas para Swagger con smiley4:ktor-swagger-ui
 * https://github.com/SMILEY4/ktor-swagger-ui
 */
fun Application.testRoutes() {
    routing {
        route("/$ENDPOINT") {
            // Get all -> /
            get({
                description = "Get All Test: Lista de mensaje de prueba"
                request {
                    queryParameter<String>("texto") {
                        description = "texto de prueba"
                        required = false // Opcional
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba como lista de mensajes"
                    }
                    HttpStatusCode.OK to {
                        description = "Listas de mensajes"
                        body<List<TestDto>> { description = "Lista de mensajes de test" }
                    }
                }
            }) {
                logger.debug { "GET /test" }
                // query params
                val texto = call.request.queryParameters["texto"] ?: "No hay texto"
                val dto = TestDto("TEST OK GET : Query: $texto")
                call.respond(HttpStatusCode.OK, listOf(dto))
            }

            // Get by id -> /{id}
            get("{id}", {
                description = "Get By Id: Mensaje de prueba"
                request {
                    pathParameter<String>("id") {
                        description = "Id del mensaje de prueba"
                        required = true // Opcional
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba"
                    }
                    HttpStatusCode.OK to {
                        description = "Mensaje con el id indicado"
                        body<TestDto> { description = "Mensaje de test encontrado" }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "No se ha indicado el id"
                        body<String> { description = "No se ha indicado el id" }
                    }
                    HttpStatusCode.NotFound to {
                        description = "No se ha encontrado el recurso"
                        body<String> { description = "El id no existe" }
                    }
                    // Y así con todos los códigos de respuesta
                }
            }) {
                logger.debug { "GET /test/{id}" }
                val id = call.parameters["id"]
                when (id) {
                    // Ejemplos de codigos de respuesta
                    null -> call.respond(HttpStatusCode.BadRequest, TestDto("No se ha indicado el id"))
                    "kaka" -> call.respond(HttpStatusCode.NotFound, TestDto("No se ha encontrado el recurso"))
                    "admin" -> call.respond(HttpStatusCode.Forbidden, TestDto("No tienes permisos"))
                    "nopuedes" -> call.respond(HttpStatusCode.Unauthorized, TestDto("No estás autorizado"))
                    "error" -> call.respond(HttpStatusCode.InternalServerError, TestDto("Error interno"))
                    "json" -> call.respond(HttpStatusCode.OK, TestDto("TEST OK GET $id"))
                    else -> call.respond(HttpStatusCode.OK, TestDto("TEST OK GET $id"))
                }
            }

            // Post -> /
            post({
                description = "Post Test: Añade mensaje de prueba"
                request {
                    body<TestDto> {
                        description = "Mensaje de prueba de actualización"
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba"
                    }
                    HttpStatusCode.Created to {
                        description = "Mensaje de prueba añadido"
                        body<TestDto> { description = "Mensaje de test añadido" }
                    }
                }

            }) {
                logger.debug { "POST /test" }
                // respond
                val input = call.receive<TestDto>()
                val dto = TestDto("TEST OK POST: ${input.message}")
                call.respond(HttpStatusCode.Created, dto)
            }

            // Put -> /{id}
            put("{id}", {
                description = "Put By Id: Mensaje de prueba"
                request {
                    pathParameter<String>("id") {
                        description = "Id del mensaje de prueba"
                        required = true // Opcional
                    }
                    body<TestDto> {
                        description = "Mensaje de prueba de actualización"
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba"
                    }
                    HttpStatusCode.OK to {
                        description = "Mensaje de prueba modificado"
                        body<TestDto> { description = "Mensaje de test modificado" }
                    }

                }
            }) {
                logger.debug { "PUT /test/{id}" }
                val id = call.parameters["id"]
                val input = call.receive<TestDto>()
                val dto = TestDto("TEST OK PUT $id : ${input.message}")
                call.respond(HttpStatusCode.OK, dto)
            }

            // Patch -> /{id}
            patch("{id}", {
                description = "Patch By Id: Mensaje de prueba"
                request {
                    pathParameter<String>("id") {
                        description = "Id del mensaje de prueba"
                        required = true // Opcional
                    }
                    body<TestDto> {
                        description = "Mensaje de prueba de actualización"
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba"
                    }
                    HttpStatusCode.OK to {
                        description = "Mensaje de prueba modificado"
                        body<TestDto> { description = "Mensaje de test modificado" }
                    }
                }
            }) {
                logger.debug { "PATCH /test/{id}" }
                val id = call.parameters["id"]
                val input = call.receive<TestDto>()
                val dto = TestDto("TEST OK PATCH $id: ${input.message}")
                call.respond(HttpStatusCode.OK, dto)
            }

            // Delete -> /{id}
            delete("{id}", {
                description = "Delete By Id: Mensaje de prueba"
                request {
                    pathParameter<String>("id") {
                        description = "Id del mensaje de prueba"
                        required = true // Opcional
                    }
                }
                response {
                    default {
                        description = "Respuesta de prueba"
                    }
                    HttpStatusCode.NoContent to {
                        description = "Mensaje de prueba modificado"
                    }
                }

            }) {
                logger.debug { "DELETE /test/{id}" }
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}