package joseluisgs.es.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.StorageException

fun StatusPagesConfig.storageStatusPages() {
    // Storage Status Pages
    exception<StorageException.FileNotFound> { call, cause ->
        call.respond(HttpStatusCode.NotFound, cause.message.toString())
    }
    exception<StorageException.FileNotSave> { call, cause ->
        call.respond(HttpStatusCode.InternalServerError, cause.message.toString())
    }
}
