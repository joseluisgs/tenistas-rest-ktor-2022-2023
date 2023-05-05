package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import com.github.michaelbull.result.mapBoth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.dto.RepresentantesPageDto
import joseluisgs.es.errors.RepresentanteError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.representantes.RepresentantesService
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
                val id = call.parameters["id"]?.toUUID()!!

                representantesService.findById(id)
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                        failure = { handleRepresentanteErrors(it) }
                    )
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }

                val dto = call.receive<RepresentanteDto>()

                representantesService.save(dto.toModel())
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.Created, it.toDto()) },
                        failure = { handleRepresentanteErrors(it) }
                    )
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!
                val dto = call.receive<RepresentanteDto>()

                representantesService.update(id, dto.toModel())
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                        failure = { handleRepresentanteErrors(it) }
                    )
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!

                representantesService.delete(id)
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.NoContent) },
                        failure = { handleRepresentanteErrors(it) }
                    )
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

private suspend fun PipelineContext<Unit, ApplicationCall>.handleRepresentanteErrors(
    error: RepresentanteError,
) {
    when (error) {
        is RepresentanteError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is RepresentanteError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is RepresentanteError.ConflictIntegrity -> call.respond(HttpStatusCode.Conflict, error.message)
    }
}