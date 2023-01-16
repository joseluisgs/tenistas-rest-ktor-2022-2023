package joseluisgs.es.models

import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

// Las notificaciones son un modelo de datos que se usan para enviar mensajes a los usuarios
// Los tipos de cambios que permito son
@Serializable
data class Notificacion<T>(
    val tipo: Tipo,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val data: T,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = LocalDateTime.now()
) {
    enum class Tipo { CREATE, UPDATE, DELETE }
}

// Mis alias, para no estar con los genéricos
typealias RepresentantesNotification = Notificacion<Representante?>