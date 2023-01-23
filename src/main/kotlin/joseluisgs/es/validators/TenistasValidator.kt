package joseluisgs.es.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.dto.TenistaCreateDto
import java.time.LocalDate


// Validadores de entrada de datos
fun RequestValidationConfig.tenistasValidation() {
    validate<TenistaCreateDto> { tenista ->
        if (tenista.nombre.isBlank()) {
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (tenista.ranking <= 0) {
            ValidationResult.Invalid("El ranking debe ser mayor que 0")
        } else if (tenista.fechaNacimiento.isAfter(LocalDate.now())) {
            ValidationResult.Invalid("La fecha de nacimiento no puede ser mayor que la actual")
        } else if (tenista.añoProfesional <= 0) {
            ValidationResult.Invalid("El año profesional debe ser mayor que 0")
        } else if (tenista.altura <= 0) {
            ValidationResult.Invalid("La altura debe ser mayor que 0")
        } else if (tenista.peso <= 0) {
            ValidationResult.Invalid("El peso debe ser mayor que 0")
        } else if (tenista.puntos <= 0) {
            ValidationResult.Invalid("Los puntos deben ser mayor que 0")
        } else if (tenista.pais.isBlank()) {
            ValidationResult.Invalid("El país no puede estar vacío")
        } else {
            ValidationResult.Valid
        }
    }
}