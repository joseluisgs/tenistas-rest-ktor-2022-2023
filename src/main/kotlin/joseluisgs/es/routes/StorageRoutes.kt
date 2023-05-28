package joseluisgs.es.routes

import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import joseluisgs.es.errors.StorageError
import joseluisgs.es.services.storage.StorageService
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.time.LocalDateTime

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

            // Aquí esta un ejemplo de como hacerlo con canales.
            // Para muchas cosas es mejor hacerlo con multipart qu everás en user
            post {
                val baseUrl =
                    call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "/$ENDPOINT/"
                // Recibimos el archivo
                logger.debug { "POST /$ENDPOINT" }
                val readChannel = call.receiveChannel()
                // Lo guardamos en disco
                val newFileName = System.currentTimeMillis().toString()
                val newFileUrl = "$baseUrl$newFileName"
                storageService.saveFile(newFileName, newFileUrl, readChannel).mapBoth(
                    success = { call.respond(HttpStatusCode.Created, it) },
                    failure = { handleStorageErrors(it) }
                )
            }

            // GET -> /{fileName}
            get("{fileName}") {
                logger.debug { "GET /$ENDPOINT/{fileName}" }

                // Recuperamos el nombre del fichero
                val fileName = call.parameters["fileName"].toString()
                // Recuperamos el fichero
                val file = storageService.getFile(fileName).mapBoth(
                    success = { call.respondFile(it) },
                    failure = { handleStorageErrors(it) }
                )
                // De esta manera lo podria visiualizar el navegador
                // call.respondFile(file)
                // si lo hago así me pide descargar
                //call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
                // Para hacer lo anterior lo mejor es saber si tiene una extensión
                // y en función de eso devolver un tipo de contenido


            }

            // DELETE /rest/uploads/
            // Si queremos rizar el rizo, podemos decir que solo borre si está autenticado
            // o cuando sea admin, etc.... quizas debas importar otro servicio
            // Estas rutas están autenticadas --> Protegidas por JWT
            authenticate {
                delete("{fileName}") {
                    logger.debug { "DELETE /$ENDPOINT/{fileName}" }

                    call.principal<JWTPrincipal>()
                    // Recuperamos el nombre del fichero
                    val fileName = call.parameters["fileName"].toString()
                    // Recuperamos el fichero
                    storageService.deleteFile(fileName).mapBoth(
                        success = { call.respond(HttpStatusCode.NoContent) },
                        failure = { handleStorageErrors(it) }
                    )
                }
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleStorageErrors(
    error: StorageError,
) {
    when (error) {
        is StorageError.FileNotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is StorageError.FileNotSave -> call.respond(HttpStatusCode.BadRequest, error.message)
    }
}