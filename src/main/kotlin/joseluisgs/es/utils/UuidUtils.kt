package joseluisgs.es.utils

import java.util.*

fun parseUuidOrNull(uuid: String): UUID? {
    return try {
        UUID.fromString(uuid)
    } catch (e: IllegalArgumentException) {
        null
    }
}