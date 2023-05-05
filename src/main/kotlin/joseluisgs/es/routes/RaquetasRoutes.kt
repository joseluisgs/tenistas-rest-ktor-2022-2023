package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import joseluisgs.es.dto.RaquetaCreateDto
import joseluisgs.es.dto.RaquetasPageDto
import joseluisgs.es.errors.RaquetaError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.raquetas.RaquetasService
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
                        .map { it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!) }
                        .let { res -> call.respond(HttpStatusCode.OK, RaquetasPageDto(page, perPage, res)) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    val res = raquetasService.findAll()
                        .toList()
                        .map { it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }

                // Obtenemos el id
                val id = call.parameters["id"]?.toUUID()!!

                raquetasService.findById(id).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.OK,
                            it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!)
                        )
                    },
                    failure = { handleRaquetaErrors(it) }
                )
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }

                val dto = call.receive<RaquetaCreateDto>()

                raquetasService.save(dto.toModel()).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.Created,
                            it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!)
                        )
                    },
                    failure = { handleRaquetaErrors(it) }
                )
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!
                val dto = call.receive<RaquetaCreateDto>()

                raquetasService.update(id, dto.toModel()).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.OK,
                            it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!)
                        )
                    },
                    failure = { handleRaquetaErrors(it) }
                )
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!

                raquetasService.delete(id).mapBoth(
                    success = { call.respond(HttpStatusCode.NoContent) },
                    failure = { handleRaquetaErrors(it) }
                )
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
                        .map { it.toDto(raquetasService.findRepresentante(it.representanteId).get()!!) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Falta el parámetro nombre")
            }

            // Get representante -> /{id}/representante
            get("{id}/representante") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}/representante" }

                val id = call.parameters["id"]?.toUUID()!!

                raquetasService.findById(id).andThen {
                    raquetasService.findRepresentante(it.representanteId)
                }.mapBoth(
                    success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                    failure = { handleRaquetaErrors(it) }
                )
            }
        }
        // Lo he sacado de esta ruta para que sea más fácil de leer desde updates!!
        // WebSockets para tiempo real
        webSocket("api/updates/raquetas") {
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
                sendSerialized("Updates Web socket: Raquetas - Tenistas API REST Ktor")
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
                raquetasService.removeSuscriptor(this.hashCode())
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleRaquetaErrors(
    error: RaquetaError,
) {
    when (error) {
        is RaquetaError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is RaquetaError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is RaquetaError.ConflictIntegrity -> call.respond(HttpStatusCode.BadRequest, error.message)
        is RaquetaError.RepresentanteNotFound -> call.respond(HttpStatusCode.BadRequest, error.message)
    }
}