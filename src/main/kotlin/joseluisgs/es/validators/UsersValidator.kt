package joseluisgs.es.validators

import io.ktor.server.plugins.requestvalidation.*
import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserLoginDto
import joseluisgs.es.dto.UserUpdateDto


// Validadores de entrada de datos
fun RequestValidationConfig.usersValidation() {

    validate<UserCreateDto> { user ->
        if (user.nombre.isBlank()) {
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (user.email.isBlank()) {
            ValidationResult.Invalid("El email no puede estar vacío")
            // validar email con regex
        } else if (!user.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$"))) {
            ValidationResult.Invalid("El email no es válido o no tiene el formato correcto")
        } else if (user.username.isBlank() && user.username.length < 3) {
            ValidationResult.Invalid("El nombre de usuario no puede estar vacío")
        } else if (user.password.isBlank() || user.password.length < 7) {
            ValidationResult.Invalid("La contraseña no puede estar vacía o ser menor de 7 caracteres")
        } else {
            ValidationResult.Valid
        }
    }

    validate<UserUpdateDto> { user ->
        if (user.nombre.isBlank()) {
            ValidationResult.Invalid("El nombre no puede estar vacío")
        } else if (user.email.isBlank()) {
            ValidationResult.Invalid("El email no puede estar vacío")
            // validar email con regex
        } else if (!user.email.contains("@")) {
            ValidationResult.Invalid("El email no es válido")
        } else if (user.username.isBlank() && user.username.length < 3) {
            ValidationResult.Invalid("El nombre de usuario no puede estar vacío")
        } else {
            ValidationResult.Valid
        }
    }

    validate<UserLoginDto> { user ->
        if (user.username.isBlank()) {
            ValidationResult.Invalid("El nombre de usuario no puede estar vacío")
        } else if (user.password.isBlank() || user.password.length < 7) {
            ValidationResult.Invalid("La contraseña no puede estar vacía o ser menor de 7 caracteres")
        } else {
            ValidationResult.Valid
        }
    }
}