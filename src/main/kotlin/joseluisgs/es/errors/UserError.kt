package joseluisgs.es.errors

// Errores de usuario
sealed class UserError(val message: String) {
    class NotFound(message: String) : UserError(message)
    class BadRequest(message: String) : UserError(message)
    class Unauthorized(message: String) : UserError(message)
    class Forbidden(message: String) : UserError(message)
}


