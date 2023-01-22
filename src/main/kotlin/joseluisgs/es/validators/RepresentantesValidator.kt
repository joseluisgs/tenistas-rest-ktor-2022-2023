package joseluisgs.es.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.dto.RepresentanteDto


// Validadores de entrada de datos
fun RequestValidationConfig.representantesValidation() {
    validate<RepresentanteDto> { representante ->
        if (representante.nombre.isBlank()) {
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (representante.email.isBlank()) {
            ValidationResult.Invalid("El email no puede estar vacío")
            // validar email con regex
        } else if (!representante.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            ValidationResult.Invalid("El email no es válido")
        } else {
            ValidationResult.Valid
        }
    }
}