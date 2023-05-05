package joseluisgs.es.errors

sealed class TenistaError(val message: String) {
    class NotFound(message: String) : TenistaError(message)
    class BadRequest(message: String) : TenistaError(message)
    class ConflictIntegrity(message: String) : TenistaError(message)
    class RaquetaNotFound(message: String) : TenistaError(message)
}
