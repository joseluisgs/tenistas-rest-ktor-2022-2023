package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.validators.raquetasValidation
import joseluisgs.es.validators.representantesValidation
import joseluisgs.es.validators.tenistasValidation
import joseluisgs.es.validators.usersValidation

fun Application.configureValidation() {
    install(RequestValidation) {
        usersValidation()
        representantesValidation()
        raquetasValidation()
        tenistasValidation()
    }
}
