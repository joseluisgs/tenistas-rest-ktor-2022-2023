package joseluisgs.es.errors

sealed class RaquetaError(val message: String) {
    class NotFound(message: String) : RaquetaError(message)
    class BadRequest(message: String) : RaquetaError(message)
    class ConflictIntegrity(message: String) : RaquetaError(message)
    class RepresentanteNotFound(message: String) : RaquetaError(message)
}
