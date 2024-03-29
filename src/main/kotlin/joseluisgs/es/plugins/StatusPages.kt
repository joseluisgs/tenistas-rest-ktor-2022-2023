package joseluisgs.es.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.exceptions.StorageException
import joseluisgs.es.services.tokens.TokenException
import joseluisgs.es.utils.UUIDException

fun Application.configureStatusPages() {
    install(StatusPages) {

        // Aquí ponemos las excepciones que queremos que se devuelvan y como
        // Empiezo con las genéricas

        // Validaciones de Datos
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }

        // UUID no válido
        exception<UUIDException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message.toString())
        }

        // Token no es válido, no existe o ha caducado. No autorizado
        exception<TokenException.InvalidTokenException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, cause.message.toString())
        }

        // Storage
        exception<StorageException.FileNotFound> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.message.toString())
        }
        exception<StorageException.FileNotSave> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message.toString())
        }

        // Ahora podemos poner los que queramos, poco a poco los vamos poniendo y limpiando el código
        // De los enrutadores, de hecho me los voy a sacar a una nuevas funciones para que quede más limpio
        // Paquete StatusPages
    }
}

