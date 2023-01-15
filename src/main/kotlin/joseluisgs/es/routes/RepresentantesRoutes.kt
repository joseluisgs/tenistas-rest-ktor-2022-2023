package joseluisgs.es.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.RepresentanteDTO
import joseluisgs.es.dto.RepresentantesPageDTO
import joseluisgs.es.repositories.representantes.RepresentantesCachedRepositoryImpl
import joseluisgs.es.repositories.representantes.RepresentantesRepositoryImpl
import joseluisgs.es.services.representantes.RepresentantesService
import joseluisgs.es.services.representantes.RepresentantesServiceImpl
import mu.KotlinLogging
import java.util.*

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
                    val id = UUID.fromString(call.parameters["id"])
                    println(id)
                    // Buscamos el representante
                    val representante = representantesService.findById(id)
                    representante?.let {
                        call.respond(HttpStatusCode.OK, it)
                    } ?: call.respond(HttpStatusCode.NotFound, "No se ha encontrado el representante con id: $id")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "El id no es válido o no tiene el formato correcto")
                }
            }

            // Post -> /
            post {
                logger.debug { "POST /test" }
                val dto = call.receive<RepresentanteDTO>()
                call.respond(HttpStatusCode.Created, "TEST OK: $dto")
            }

            /*

           // Put -> /{id}
           put("{id}") {
               logger.debug { "PUT /test/{id}" }
               val id = call.parameters["id"]
               call.respond(HttpStatusCode.OK, "TEST OK PUT $id")
           }

           // Patch -> /{id}
           patch("{id}") {
               logger.debug { "PATCH /test/{id}" }
               val id = call.parameters["id"]
               call.respond(HttpStatusCode.OK, "TEST OK PATCH $id")
           }

           // Delete -> /{id}
           delete("{id}") {
               logger.debug { "DELETE /test/{id}" }
               val id = call.parameters["id"]
               call.respond(HttpStatusCode.OK, "TEST OK DELETE $id")
           }*/
        }
    }
}