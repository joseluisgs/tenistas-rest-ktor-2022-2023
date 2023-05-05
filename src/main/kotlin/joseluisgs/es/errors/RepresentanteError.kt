package joseluisgs.es.errors

// Errores de representante
sealed class RepresentanteError(val message: String) {
    class NotFound(message: String) : RepresentanteError(message)
    class BadRequest(message: String) : RepresentanteError(message)
    class ConflictIntegrity(message: String) : RepresentanteError(message)
}
