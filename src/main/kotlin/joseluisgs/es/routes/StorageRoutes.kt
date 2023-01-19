package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.exceptions.FileNotSaveException
import joseluisgs.es.services.storage.StorageService
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/storage" // Ruta de acceso, puede aunar un recurso

fun Application.storageRoutes() {

    val storageService: StorageService by inject()

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
            post {
                // Recibimos el archivo
                logger.debug { "POST /$ENDPOINT" }
                try {
                    val readChannel = call.receiveChannel()
                    // Lo guardamos en disco
                    val fileName = UUID.randomUUID().toString()
                    val res = storageService.saveFile(fileName, readChannel)
                    // Respondemos
                    call.respond(HttpStatusCode.OK, res)
                } catch (e: FileNotSaveException) {
                    call.respond(HttpStatusCode.InternalServerError, e.message.toString())
                } catch (e: Exception) {
                    call.respondText("Error: ${e.message}")
                }
            }
        }
    }
}