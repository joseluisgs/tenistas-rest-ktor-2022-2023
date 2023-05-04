package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
/**
 * RepresentanteException
 * @param message: String Mensaje de la excepción
 */
sealed class RepresentanteException(message: String) : RuntimeException(message) {
    class NotFound(message: String) : RepresentanteException(message)
    class BadRequest(message: String) : RepresentanteException(message)
    class ConflictIntegrity(message: String) : RepresentanteException(message)
}
