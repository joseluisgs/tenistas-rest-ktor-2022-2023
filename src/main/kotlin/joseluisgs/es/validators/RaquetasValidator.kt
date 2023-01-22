package joseluisgs.es.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.dto.RaquetaCreateDto


// Validadores de entrada de datos
fun RequestValidationConfig.raquetasValidation() {
    validate<RaquetaCreateDto> { raqueta ->
        if (raqueta.marca.isBlank()) {
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (raqueta.represetanteId.toString().isBlank()) {
            ValidationResult.Invalid("El identificador del representante no puede estar vacío")
            // validar email con regex
        } else if (raqueta.precio < 0) {
            ValidationResult.Invalid("El precio no puede ser negativo")
        } else {
            ValidationResult.Valid
        }
    }
}