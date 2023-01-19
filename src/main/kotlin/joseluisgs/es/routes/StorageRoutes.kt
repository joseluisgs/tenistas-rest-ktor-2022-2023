package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/storage" // Ruta de acceso, puede aunar un recurso

fun Application.storageRoutes() {
    routing {
        route("/$ENDPOINT") {
            // Get all -> /
            get("check") {
                logger.debug { "GET ALL /$ENDPOINT/check" }
                // respond, a veces no es necesario un dto, si lo tenemos muy claro
                // con un mapa de datos es suficiente
                call.respond(
                    HttpStatusCode.OK,
                    mapOf(
                        "status" to "OK",
                        "message" to "Storage API REST Ktor. 2º DAM",
                        "createdAt" to LocalDateTime.now().toString()
                    )
                )
            }
        }
    }
}