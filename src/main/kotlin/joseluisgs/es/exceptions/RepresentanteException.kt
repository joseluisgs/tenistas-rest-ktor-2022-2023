package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
/**
 * RepresentanteException
 * @param message: String Mensaje de la excepción
 */
sealed class RepresentanteException(message: String) : RuntimeException(message)
class RepresentanteNotFoundException(message: String) : RepresentanteException(message)
class RepresentanteBadRequestException(message: String) : RepresentanteException(message)
class RepresentanteConflictIntegrityException(message: String) : RepresentanteException(message)