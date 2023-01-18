package joseluisgs.es.utils

import java.util.*

class UUIDException(message: String) : Exception(message)

fun String.toUUID(): UUID {
    return try {
        UUID.fromString(this.trim())
    } catch (e: IllegalArgumentException) {
        throw UUIDException("El id no es válido o no está en el formato UUID")
    }
}