package joseluisgs.es.exceptions

sealed class DataBaseException(message: String?) : RuntimeException(message)
class DataBaseIntegrityViolationException(message: String? = null) : DataBaseException(message)