package joseluisgs.es.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import joseluisgs.es.services.tokens.TokenException
import joseluisgs.es.statuspages.*
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

        // Ahora podemos poner los que queramos, poco a poco los vamos poniendo y limpiando el código
        // De los enrutadores, de hecho me los voy a sacar a una nuevas funciones para que quede más limpio
        // Paquete StatusPages
        representantesStatusPages()
        raquetasStatusPages()
        tenistasStatusPages()
        usersStatusPages()
        storageStatusPages()
    }
}

