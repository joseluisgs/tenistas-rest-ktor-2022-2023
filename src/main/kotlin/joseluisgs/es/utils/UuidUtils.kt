package joseluisgs.es.utils

import java.util.*

class UuidException(message: String) : Exception(message)

fun String.toUUID(): UUID {
    return try {
        UUID.fromString(this.trim())
    } catch (e: IllegalArgumentException) {
        throw UuidException("El id no es válido o no está en el formato UUID")
    }
}