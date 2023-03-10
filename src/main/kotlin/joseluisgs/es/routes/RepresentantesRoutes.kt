package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.dto.RepresentantesPageDto
import joseluisgs.es.exceptions.RepresentanteConflictIntegrityException
import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.representantes.RepresentantesService
import joseluisgs.es.utils.UUIDException
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/representantes" // Ruta de acceso, puede aunar un recurso

fun Application.representantesRoutes() {

    // Inyección nada mas iniciarse
    //val representantesService: RepresentantesService = koinGet()
    // Iyeccion de dependencias Lazy, cuandos se usa la primera vez lazy
    val representantesService: RepresentantesService by inject()

    routing {
        route("/$ENDPOINT") {
            // Get all -> /
            get {
                // Tenemos QueryParams ??
                val page = call.request.queryParameters["page"]?.toIntOrNull()
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10

                if (page != null && page > 0) {
                    logger.debug { "GET ALL /$ENDPOINT?page=$page&perPage=$perPage" }
                    // Procesamos el flow
                    representantesService.findAllPageable(page - 1, perPage)
                        .toList()
                        .map { it.toDto() }
                        .let { res -> call.respond(HttpStatusCode.OK, RepresentantesPageDto(page, perPage, res)) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    representantesService.findAll()
                        .toList().map { it.toDto() }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }
                // Obtenemos el id
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val representante = representantesService.findById(id)
                    call.respond(HttpStatusCode.OK, representante.toDto())
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: UUIDException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }
                try {
                    val dto = call.receive<RepresentanteDto>()
                    val representante = representantesService.save(dto.toModel())
                    call.respond(HttpStatusCode.Created, representante.toDto())
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                }
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val dto = call.receive<RepresentanteDto>()
                    val representante = representantesService.update(id, dto.toModel())
                    call.respond(HttpStatusCode.OK, representante.toDto())
                    // Vamos a captar las excepciones de nuestro dominio
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                } catch (e: UUIDException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val representante = representantesService.delete(id)
                    // Decidimos si devolver un 200 o un 204 (No Content)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RepresentanteConflictIntegrityException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                } catch (e: UUIDException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // Otros métodos de búsqueda
            // Get by nombre -> /find?nombre={nombre}
            get("find") {
                // Es similar a la página, podemos crear las busquedas que queramos o necesitemos
                // se puede combinar varias
                logger.debug { "GET BY NOMBRE /$ENDPOINT/find?nombre={nombre}" }
                val nombre = call.request.queryParameters["nombre"]
                nombre?.let {
                    representantesService.findByNombre(nombre)
                        .toList()
                        .map { it.toDto() }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Falta el parámetro nombre")
            }
        }
        // Lo he sacado de esta ruta para que sea más fácil de leer desde updates!!
        // WebSockets para tiempo real
        webSocket("api/updates/representantes") {
            try {
                // Podría usar un uuid para identificar al cliente, pero mejor su hasCode()
                // si no te gusta que lo haya llamado con la función, puedes pasar el objeto this, si
                // lo cabias, pero para eso Kotlin es un lenguaje con características de funcional, acustúmbrate :)
                representantesService.addSuscriptor(this.hashCode()) {
                    // Al darnos de alta con esta función,
                    // cuando la invoquemos mandará los datos serializados que le pasemos
                    // https://ktor.io/docs/websocket-serialization.html#send_data
                    sendSerialized(it) // Enviamos las cosas
                }
                sendSerialized("Updates Web socket: Representantes - Tenistas API REST Ktor")
                // Por cada mensaje que nos llegue
                for (frame in incoming) {
                    if (frame.frameType == FrameType.CLOSE) {
                        break
                        // Por cada mensaje que nos llegue, lo mostramos por consola
                    } else if (frame is Frame.Text) {
                        logger.debug { "Mensaje recibido por WS Representantes: ${frame.readText()}" }
                    }
                }
            } finally {
                representantesService.removeSuscriptor(this.hashCode())
            }
        }
    }
}