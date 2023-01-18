package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
sealed class UserException(message: String) : RuntimeException(message)
class UserNotFoundException(message: String) : UserException(message)
class UserBadRequestException(message: String) : UserException(message)
class UserUnauthorizedException(message: String) : UserException(message)
class UserForbiddenException(message: String) : UserException(message)

