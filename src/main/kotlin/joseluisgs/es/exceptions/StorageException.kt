package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
sealed class StorageException(message: String) : RuntimeException(message)
class StorageFileNotFoundException(message: String) : StorageException(message)
class StorageFileNotSaveException(message: String) : StorageException(message)


