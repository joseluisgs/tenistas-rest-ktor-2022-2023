package joseluisgs.es.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.UserException

fun StatusPagesConfig.usersStatusPages() {
    // Users Status Pages
    exception<UserException.NotFound> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.toString())
    }
    exception<UserException.BadRequest> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.message.toString())
    }
    exception<UserException.Forbidden> { call, cause ->
        call.respond(HttpStatusCode.Forbidden, cause.message.toString())
    }
    exception<UserException.Unauthorized> { call, cause ->
        call.respond(HttpStatusCode.Unauthorized, cause.message.toString())
    }
}
