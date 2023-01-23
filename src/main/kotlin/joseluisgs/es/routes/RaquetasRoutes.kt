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
import joseluisgs.es.dto.RaquetaCreateDto
import joseluisgs.es.dto.RaquetasPageDto
import joseluisgs.es.exceptions.RaquetaConflictIntegrityException
import joseluisgs.es.exceptions.RaquetaNotFoundException
import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.raquetas.RaquetasService
import joseluisgs.es.utils.UUIDException
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/raquetas" // Ruta de acceso, puede aunar un recurso

fun Application.raquetasRoutes() {

    // Inyección nada mas iniciarse
    //val representantesService: RepresentantesService = koinGet()
    // Iyeccion de dependencias Lazy, cuandos se usa la primera vez lazy
    val raquetasService: RaquetasService by inject()

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
                    val res = raquetasService.findAllPageable(page - 1, perPage)
                        .toList()
                        .map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, RaquetasPageDto(page, perPage, res)) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    val res = raquetasService.findAll()
                        .toList()
                        .map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }
                // Obtenemos el id
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val raqueta = raquetasService.findById(id)
                    call.respond(
                        HttpStatusCode.OK, raqueta.toDto(
                            raquetasService.findRepresentante(raqueta.represetanteId)
                        )
                    )
                } catch (e: RaquetaNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: UUIDException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }
                try {
                    val dto = call.receive<RaquetaCreateDto>()
                    val raqueta = raquetasService.save(dto.toModel())
                    call.respond(
                        HttpStatusCode.Created, raqueta.toDto(
                            raquetasService.findRepresentante(raqueta.represetanteId)
                        )
                    )
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())

                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                }
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val dto = call.receive<RaquetaCreateDto>()
                    val representante = raquetasService.update(id, dto.toModel())
                    call.respond(
                        HttpStatusCode.OK, representante.toDto(
                            raquetasService.findRepresentante(representante.represetanteId)
                        )
                    )
                    // Vamos a captar las excepciones de nuestro dominio
                } catch (e: RaquetaNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
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
                    val representante = raquetasService.delete(id)
                    // Decidimos si devolver un 200 o un 204 (No Content)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: RaquetaNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RaquetaConflictIntegrityException) {
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
                logger.debug { "GET BY MARCA /$ENDPOINT/find?marca={marca}" }
                val marca = call.request.queryParameters["marca"]
                marca?.let {
                    val res = raquetasService.findByMarca(marca)
                        .toList()
                        .map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Falta el parámetro nombre")
            }

            // Get representante -> /{id}/representante
            get("{id}/representante") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}/representante" }
                // Obtenemos el id
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val raqueta = raquetasService.findById(id)
                    val representante = raquetasService.findRepresentante(raqueta.represetanteId)
                    call.respond(HttpStatusCode.OK, representante.toDto())
                } catch (e: RaquetaNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                } catch (e: UUIDException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // WebSockets para tiempo real
            webSocket("/updates") {
                try {
                    // Podría usar un uuid para identificar al cliente, pero mejor su hasCode()
                    // si no te gusta que lo haya llamado con la función, puedes pasar el objeto this, si
                    // lo cabias, pero para eso Kotlin es un lenguaje con características de funcional, acustúmbrate :)
                    raquetasService.addSuscriptor(this.hashCode()) {
                        // Al darnos de alta con esta función,
                        // cuando la invoquemos mandará los datos serializados que le pasemos
                        // https://ktor.io/docs/websocket-serialization.html#send_data
                        sendSerialized(it) // Enviamos las cosas
                    }
                    // Por cada mensaje que nos llegue
                    for (frame in incoming) {
                        if (frame.frameType == FrameType.CLOSE) {
                            break
                            // Por cada mensaje que nos llegue, lo mostramos por consola
                        } else if (frame is Frame.Text) {
                            logger.info { "Mensaje recibido por WS Representantes: ${frame.readText()}" }
                        }
                    }
                } finally {
                    raquetasService.removeSuscriptor(this.hashCode())
                }
            }
        }
    }
}