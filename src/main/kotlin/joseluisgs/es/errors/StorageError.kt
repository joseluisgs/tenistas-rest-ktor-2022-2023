package joseluisgs.es.errors

sealed class StorageError(val message: String) {
    class FileNotFound(message: String) : StorageError(message)
    class FileNotSave(message: String) : StorageError(message)
}
