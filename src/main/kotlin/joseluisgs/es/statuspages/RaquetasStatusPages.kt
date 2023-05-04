package joseluisgs.es.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.RaquetaException

fun StatusPagesConfig.raquetasStatusPages() {
    // Raquetas
    exception<RaquetaException.NotFound> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.toString())
    }
    exception<RaquetaException.ConflictIntegrity> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
    exception<RaquetaException.BadRequest> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
    exception<RaquetaException.RepresentanteNotFound> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
}
