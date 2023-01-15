package joseluisgs.es.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.dto.RepresentanteDTO


// Validadores de entrada de datos
fun RequestValidationConfig.representantesValidation() {
    validate<RepresentanteDTO> { representante ->
        if (representante.nombre.isBlank()) {
            println("Nombre no puede estar vacío")
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (representante.email.isBlank()) {
            println("Email no puede estar vacío")
            ValidationResult.Invalid("El email no puede estar vacío")
            // validar email con regex
        } else if (!representante.email.contains("@")) {
            ValidationResult.Invalid("El email no es válido")
        } else {
            ValidationResult.Valid
        }
    }
}