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
import joseluisgs.es.dto.TenistaCreateDto
import joseluisgs.es.dto.TenistasPageDto
import joseluisgs.es.errors.TenistaError
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
                    tenistasService.findAllPageable(page - 1, perPage)
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId).get()) }
                        .let { res -> call.respond(HttpStatusCode.OK, TenistasPageDto(page, perPage, res)) }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    tenistasService.findAll()
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId).get()) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!

                tenistasService.findById(id).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.OK,
                            it.toDto(tenistasService.findRaqueta(it.raquetaId).get())
                        )
                    },
                    failure = { handleTenistaErrors(it) }
                )
            }

            // Post -> /
            post {
                logger.debug { "POST /$ENDPOINT" }

                val dto = call.receive<TenistaCreateDto>()

                tenistasService.save(dto.toModel()).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.Created,
                            it.toDto(tenistasService.findRaqueta(it.raquetaId).get())
                        )
                    },
                    failure = { handleTenistaErrors(it) }
                )
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!
                val dto = call.receive<TenistaCreateDto>()

                tenistasService.update(id, dto.toModel()).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.OK,
                            it.toDto(tenistasService.findRaqueta(it.raquetaId).get())
                        )
                    },
                    failure = { handleTenistaErrors(it) }
                )
            }

            // Delete -> /{id}
            delete("{id}") {
                logger.debug { "DELETE /$ENDPOINT/{id}" }

                val id = call.parameters["id"]?.toUUID()!!

                tenistasService.delete(id).mapBoth(
                    success = { call.respond(HttpStatusCode.NoContent) },
                    failure = { handleTenistaErrors(it) }
                )
            }

            // Otros métodos de búsqueda
            // Get by nombre -> /find?nombre={nombre}
            get("find") {
                // Es similar a la página, podemos crear las busquedas que queramos o necesitemos
                // se puede combinar varias

                logger.debug { "GET BY MARCA /$ENDPOINT/find?nombre={nombre}" }
                val nombre = call.request.queryParameters["nombre"]

                nombre?.let {
                    tenistasService.findByNombre(nombre)
                        .toList()
                        .map { it.toDto(tenistasService.findRaqueta(it.raquetaId).get()) }
                        .let { res -> call.respond(HttpStatusCode.OK, res) }
                } ?: call.respond(HttpStatusCode.BadRequest, "Falta el parámetro nombre")
            }

            // Get representante -> /{id}/representante
            get("{id}/raqueta") {
                logger.debug { "GET BY ID /$ENDPOINT/{id}/raqueta" }

                val id = call.parameters["id"]?.toUUID()!!

                tenistasService.findById(id).andThen {
                    tenistasService.findRaqueta(it.raquetaId)
                }.mapBoth(
                    success = {
                        it?.let {
                            call.respond(HttpStatusCode.OK, it.toTenistaDto())
                        } ?: call.respond(HttpStatusCode.NotFound, "No se ha encontrado la raqueta")
                    },
                    failure = { handleTenistaErrors(it) }
                )
            }

            get("/ranking/{ranking}") {
                logger.debug { "GET BY ID /$ENDPOINT/ranking/{ranking}" }

                val ranking = call.parameters["ranking"]?.toIntOrNull() ?: 0

                tenistasService.findByRanking(ranking).mapBoth(
                    success = {
                        call.respond(
                            HttpStatusCode.OK,
                            it.toDto(tenistasService.findRaqueta(it.raquetaId).get())
                        )
                    },
                    failure = { handleTenistaErrors(it) }
                )
            }
        }

        // WebSockets para tiempo real
        webSocket("api/updates/tenistas") {
            sendSerialized("Updates Web socket: Tenistas - Tenistas API REST Ktor")
            // actualizaciones de del estado y reaccionamos
            tenistasService.notificationState.collect {
                // Cuando llegue un nuevo estado, lo enviamos serializado
                sendSerialized(it)
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.handleTenistaErrors(
    error: TenistaError,
) {
    when (error) {
        is TenistaError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is TenistaError.ConflictIntegrity -> call.respond(HttpStatusCode.Conflict, error.message)
        is TenistaError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is TenistaError.RaquetaNotFound -> call.respond(HttpStatusCode.NotFound, error.message)
    }
}