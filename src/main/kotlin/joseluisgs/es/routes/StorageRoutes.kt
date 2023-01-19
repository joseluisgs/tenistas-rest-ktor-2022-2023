package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.exceptions.StorageFileNotFoundException
import joseluisgs.es.exceptions.StorageFileNotSaveException
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
                } catch (e: StorageFileNotSaveException) {
                    call.respond(HttpStatusCode.InternalServerError, e.message.toString())
                } catch (e: Exception) {
                    call.respondText("Error: ${e.message}")
                }
            }

            // GET -> /{fileName}
            get("{fileName}") {
                logger.debug { "GET /$ENDPOINT/{fileName}" }
                try {
                    // Recuperamos el nombre del fichero
                    val fileName = call.parameters["fileName"].toString()
                    // Recuperamos el fichero
                    val file = storageService.getFile(fileName)
                    // De esta manera lo podria visiualizar el navegador
                    // call.respondFile(file)
                    // si lo hago así me pide descargar
                    //call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
                    // Para hacer lo anterior lo mejor es saber si tiene una extensión
                    // y en función de eso devolver un tipo de contenido
                    call.respondFile(file)
                } catch (e: StorageFileNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                }
            }

            // DELETE /rest/uploads/
            // Si queremos rizar el rizo, podemos decir que solo borre si está autenticado
            // o cuando sea admin, etc.... quizas debas importar otro servicio
            // Estas rutas están autenticadas --> Protegidas por JWT
            authenticate {
                delete("{fileName}") {
                    logger.debug { "DELETE /$ENDPOINT/{fileName}" }
                    try {
                        val jwt = call.principal<JWTPrincipal>()
                        // Recuperamos el nombre del fichero
                        val fileName = call.parameters["fileName"].toString()
                        // Recuperamos el fichero
                        storageService.deleteFile(fileName)
                        // Respondemos
                        call.respond(HttpStatusCode.NoContent)
                    } catch (e: StorageFileNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, e.message.toString())
                    }
                }
            }
        }
    }
}