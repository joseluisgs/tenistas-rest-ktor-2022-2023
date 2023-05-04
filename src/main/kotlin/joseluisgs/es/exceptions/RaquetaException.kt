package joseluisgs.es.exceptions

sealed class RaquetaException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : RaquetaException(message)
    class BadRequest(message: String) : RaquetaException(message)
    class ConflictIntegrity(message: String) : RaquetaException(message)
    class RepresentanteNotFound(message: String) : RaquetaException(message)
}
