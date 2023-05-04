package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
sealed class UserException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : UserException(message)
    class BadRequest(message: String) : UserException(message)
    class Unauthorized(message: String) : UserException(message)
    class Forbidden(message: String) : UserException(message)
}


