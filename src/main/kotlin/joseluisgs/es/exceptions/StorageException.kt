package joseluisgs.es.exceptions

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
sealed class StorageException(message: String) : RuntimeException(message)
class FileNotFoundException(message: String) : StorageException(message)
class FileNotSaveException(message: String) : StorageException(message)


