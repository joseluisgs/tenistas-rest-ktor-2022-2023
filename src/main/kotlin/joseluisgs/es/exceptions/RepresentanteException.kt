package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
/**
 * RepresentanteException
 * @param message: String Mensaje de la excepción
 */
sealed class RepresentanteException(message: String) : RuntimeException(message) {
    class NotFoundException(message: String) : RepresentanteException(message)
    class BadRequestException(message: String) : RepresentanteException(message)
    class ConflictIntegrityException(message: String) : RepresentanteException(message)
}
