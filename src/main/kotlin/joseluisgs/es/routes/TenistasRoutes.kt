package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import joseluisgs.es.dto.TenistaCreateDto
import joseluisgs.es.dto.TenistasPageDto
import joseluisgs.es.exceptions.TenistaException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.mappers.toTenistaDto
import joseluisgs.es.services.tenistas.TenistasService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/tenistas" // Ruta de acceso, puede aunar un recurso

fun Application.tenistasRoutes() {

    // Inyección nada mas iniciarse
    //val representantesService: RepresentantesService = koinGet()
    // Iyeccion de dependencias Lazy, cuandos se usa la primera vez lazy
    val tenistasService: TenistasService by inject()

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
                    val res = tenistasService.findAllPageable(page - 1, perPage)
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, TenistasPageDto(page, perPage, res)) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    val res = tenistasService.findAll()
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }
                // Obtenemos el id
                val id = call.parameters["id"]?.toUUID()!!
                val tenista = tenistasService.findById(id)
                call.respond(
                    HttpStatusCode.OK, tenista.toDto(
                        tenistasService.findRaqueta(tenista.raquetaId)
                    )
                )
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }

                val dto = call.receive<TenistaCreateDto>()
                val tenista = tenistasService.save(dto.toModel())
                call.respond(
                    HttpStatusCode.Created, tenista.toDto(
                        tenistasService.findRaqueta(tenista.raquetaId)
                    )
                )
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }
                val id = call.parameters["id"]?.toUUID()!!
                val dto = call.receive<TenistaCreateDto>()
                val tenista = tenistasService.update(id, dto.toModel())
                call.respond(
                    HttpStatusCode.OK, tenista.toDto(
                        tenistasService.findRaqueta(tenista.raquetaId)
                    )
                )
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!
                val representante = tenistasService.delete(id)
                // Decidimos si devolver un 200 o un 204 (No Content)
                call.respond(HttpStatusCode.NoContent)
            }

            // Otros métodos de búsqueda
            // Get by nombre -> /find?nombre={nombre}
            get("find") {
                // Es similar a la página, podemos crear las busquedas que queramos o necesitemos
                // se puede combinar varias
                logger.debug { "GET BY MARCA /$ENDPOINT/find?nombre={nombre}" }
                val nombre = call.request.queryParameters["nombre"]
                nombre?.let {
                    val res = tenistasService.findByNombre(nombre)
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Falta el parámetro nombre")
            }

            // Get representante -> /{id}/representante
            get("{id}/raqueta") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}/raqueta" }
                // Obtenemos el id

                val id = call.parameters["id"]?.toUUID()!!
                val tenista = tenistasService.findById(id)
                val raqueta = tenistasService.findRaqueta(tenista.raquetaId)
                raqueta?.let {
                    call.respond(HttpStatusCode.OK, raqueta.toTenistaDto())
                } ?: throw TenistaException.RaquetaNotFound("No se ha encontrado la raqueta")
            }

            get("/ranking/{ranking}") {
                logger.debug { "GET BY ID /$ENDPOINT/ranking/{ranking}" }
                // Obtenemos el id

                val id = call.parameters["ranking"]?.toIntOrNull() ?: 0
                val tenista = tenistasService.findByRanking(id)
                call.respond(
                    HttpStatusCode.OK, tenista.toDto(
                        tenistasService.findRaqueta(tenista.raquetaId)
                    )
                )

            }
        }

        // WebSockets para tiempo real
        webSocket("api/updates/tenistas") {
            try {
                // Podría usar un uuid para identificar al cliente, pero mejor su hasCode()
                // si no te gusta que lo haya llamado con la función, puedes pasar el objeto this, si
                // lo cabias, pero para eso Kotlin es un lenguaje con características de funcional, acustúmbrate :)
                tenistasService.addSuscriptor(this.hashCode()) {
                    // Al darnos de alta con esta función,
                    // cuando la invoquemos mandará los datos serializados que le pasemos
                    // https://ktor.io/docs/websocket-serialization.html#send_data
                    sendSerialized(it) // Enviamos las cosas
                }

                sendSerialized("Updates Web socket: Tenistas - Tenistas API REST Ktor")
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
                tenistasService.removeSuscriptor(this.hashCode())
            }
        }
    }
}