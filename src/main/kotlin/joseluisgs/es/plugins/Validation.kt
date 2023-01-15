package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.validators.representantesValidation

fun Application.configureValidation() {
    install(RequestValidation) {
        representantesValidation()
    }
}
