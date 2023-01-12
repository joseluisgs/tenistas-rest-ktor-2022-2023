package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun Application.testRoutes() {

    routing {
        route("rest/test") {
            // Get all -> /
            get {
                logger.debug { "GET /test" }
                call.respond(HttpStatusCode.OK, "TEST OK GET")
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET /test/{id}" }
                val id = call.parameters["id"]
                when (id) {
                    null -> call.respond(HttpStatusCode.BadRequest, "No se ha mandado id")
                    "kaka" -> call.respond(HttpStatusCode.NotFound, "No se ha encontrado el recurso")
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
                when (id) {
                    null -> call.respond(HttpStatusCode.BadRequest, "No se ha mandado id")
                    "kaka" -> call.respond(HttpStatusCode.NotFound, "No se ha encontrado el recurso")
                    else -> call.respond(HttpStatusCode.OK, "TEST OK PUT $id")
                }
            }

            // Patch -> /{id}
            patch("{id}") {
                logger.debug { "PATCH /test/{id}" }
                val id = call.parameters["id"]
                when (id) {
                    null -> call.respond(HttpStatusCode.BadRequest, "No se ha mandado id")
                    "kaka" -> call.respond(HttpStatusCode.NotFound, "No se ha encontrado el recurso")
                    else -> call.respond(HttpStatusCode.OK, "TEST OK PATCH $id")
                }
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /test/{id}" }
                val id = call.parameters["id"]
                when (id) {
                    null -> call.respond(HttpStatusCode.BadRequest, "No se ha mandado id")
                    "kaka" -> call.respond(HttpStatusCode.NotFound, "No se ha encontrado el recurso")
                    else -> call.respond(HttpStatusCode.OK, "TEST OK DELETE $id")
                }
            }
        }
    }
}