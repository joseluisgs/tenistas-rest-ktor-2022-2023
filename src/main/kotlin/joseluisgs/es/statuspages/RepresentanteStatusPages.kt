package joseluisgs.es.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.RepresentanteException

fun StatusPagesConfig.representantesStatusPages() {
    // Representantes
    exception<RepresentanteException.NotFound> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.toString())
    }
    exception<RepresentanteException.ConflictIntegrity> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
}
