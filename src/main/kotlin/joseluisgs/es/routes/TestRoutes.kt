package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/test" // Ruta de acceso, puede aunar un recurso

fun Application.testRoutes() {
    routing {
        route("/$ENDPOINT") {
            // Get all -> /
            get {
                logger.debug { "GET /test" }
                // respond
                call.respond(HttpStatusCode.OK, "TEST OK GET")
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET /test/{id}" }
                val id = call.parameters["id"]
                when (id) {
                    // Ejemplos de codigos de respuesta
                    null -> call.respond(HttpStatusCode.BadRequest, "No se ha mandado id")
                    "kaka" -> call.respond(HttpStatusCode.NotFound, "No se ha encontrado el recurso")
                    "admin" -> call.respond(HttpStatusCode.Forbidden, "No tienes permisos")
                    "nopuedes" -> call.respond(HttpStatusCode.Unauthorized, "Timeout")
                    "error" -> call.respond(HttpStatusCode.InternalServerError, "Error interno")
                    "json" -> call.respond(HttpStatusCode.OK, mapOf("id" to id, "message" to "TEST OK GET $id"))
                    else -> call.respond(HttpStatusCode.OK, "TEST OK GET $id")
                }
            }

            // Post -> /
            post {
                logger.debug { "POST /test" }
                call.respond(HttpStatusCode.Created, "TEST OK")
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /test/{id}" }
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "TEST OK PUT $id")
            }

            // Patch -> /{id}
            patch("{id}") {
                logger.debug { "PATCH /test/{id}" }
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "TEST OK PATCH $id")
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /test/{id}" }
                val id = call.parameters["id"]
                call.respond(HttpStatusCode.OK, "TEST OK DELETE $id")
            }
        }
    }
}