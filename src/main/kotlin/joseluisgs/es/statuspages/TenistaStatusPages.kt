package joseluisgs.es.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.TenistaException

fun StatusPagesConfig.tenistasStatusPages() {
    // Tenistas Status Pages
    exception<TenistaException.NotFound> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.toString())
    }
    exception<TenistaException.ConflictIntegrity> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
    exception<TenistaException.BadRequest> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
    exception<TenistaException.RaquetaNotFound> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
}
