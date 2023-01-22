package joseluisgs.es.exceptions

sealed class RaquetaException(message: String) : RuntimeException(message)
class RaquetaNotFoundException(message: String) : RaquetaException(message)
class RaquetaBadRequestException(message: String) : RaquetaException(message)