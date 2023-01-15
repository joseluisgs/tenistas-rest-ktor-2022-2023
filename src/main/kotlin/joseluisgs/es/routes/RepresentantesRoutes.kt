package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.RepresentanteDTO
import joseluisgs.es.dto.RepresentantesPageDTO
import joseluisgs.es.exceptions.RepresentanteNotFoundException
import joseluisgs.es.mappers.toModel
import joseluisgs.es.repositories.representantes.RepresentantesCachedRepositoryImpl
import joseluisgs.es.repositories.representantes.RepresentantesRepositoryImpl
import joseluisgs.es.services.representantes.RepresentantesService
import joseluisgs.es.services.representantes.RepresentantesServiceImpl
import joseluisgs.es.utils.UuidException
import joseluisgs.es.utils.toUUID
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "rest/representantes" // Ruta de acceso, puede aunar un recurso

fun Application.representantesRoutes() {
    // Dependencas inyectadas manualmente
    val representantesService: RepresentantesService = RepresentantesServiceImpl(
        RepresentantesCachedRepositoryImpl(RepresentantesRepositoryImpl())
    )

    routing {
        route("/$ENDPOINT") {
            // Get all -> /
            get {
                // Tenemos QueryParams ??
                val page = call.request.queryParameters["page"]?.toIntOrNull()
                val perPage = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
                if (page != null && page > 0) {
                    logger.debug { "GET ALL /$ENDPOINT?page=$page&perPage=$perPage" }
                    representantesService.findAllPageable(page - 1, perPage).collect {
                        val dto = RepresentantesPageDTO(
                            page = page,
                            perPage = perPage,
                            data = it
                        )
                        call.respond(HttpStatusCode.OK, dto)
                    }
                } else {
                    logger.debug { "GET ALL /$ENDPOINT" }
                    representantesService.findAll().collect {
                        call.respond(HttpStatusCode.OK, it)
                    }
                }
            }

            // Get by id -> /{id}
            get("{id}") {
                logger.debug { "GET /test/{id}" }
                // Obtenemos el id
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val representante = representantesService.findById(id)
                    call.respond(HttpStatusCode.OK, representante)
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: UuidException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            // Post -> /
            post {
                logger.debug { "POST /test" }
                try {
                    val dto = call.receive<RepresentanteDTO>()
                    val representante = representantesService.save(dto.toModel())
                    call.respond(HttpStatusCode.Created, representante)
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                }
            }

            // Put -> /{id}
            put("{id}") {
                logger.debug { "PUT /test/{id}" }
                try {
                    val id = call.parameters["id"]?.toUUID()!!
                    val dto = call.receive<RepresentanteDTO>()
                    val representante = representantesService.update(id, dto.toModel())
                    call.respond(HttpStatusCode.OK, representante)
                    // Vamos a captar las excepciones de nuestro dominio
                } catch (e: RepresentanteNotFoundException) {
                    call.respond(HttpStatusCode.NotFound, e.message.toString())
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                } catch (e: UuidException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }
            /*

            // Delete -> /{id}
            delete("{id}") {
               logger.debug { "DELETE /test/{id}" }
               val id = call.parameters["id"]
               call.respond(HttpStatusCode.OK, "TEST OK DELETE $id")
            }*/
        }
    }
}