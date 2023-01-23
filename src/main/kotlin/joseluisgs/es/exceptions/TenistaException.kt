package joseluisgs.es.exceptions

sealed class TenistaException(message: String) : RuntimeException(message)
class TenistaNotFoundException(message: String) : TenistaException(message)
class TenistaBadRequestException(message: String) : TenistaException(message)