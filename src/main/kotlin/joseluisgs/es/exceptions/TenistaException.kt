package joseluisgs.es.exceptions

sealed class TenistaException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : TenistaException(message)
    class BadRequest(message: String) : TenistaException(message)
    class ConflictIntegrity(message: String) : TenistaException(message)
    class RaquetaNotFound(message: String) : TenistaException(message)
}
