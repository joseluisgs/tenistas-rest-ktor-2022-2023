package joseluisgs.es.models

import joseluisgs.es.dto.RaquetaDto
import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.dto.TenistaDto
import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

// Las notificaciones son un modelo de datos que se usan para enviar mensajes a los usuarios
// Los tipos de cambios que permito son
@Serializable
data class Notificacion<T>(
    val entity: String,
    val tipo: Tipo,
    @Serializable(with = UUIDSerializer::class)
    val id: UUID?,
    val data: T?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    enum class Tipo { CREATE, UPDATE, DELETE }
}

// Mis alias, para no estar con los genéricos, mando el DTO por que es lo que quiero que se envíe con sus datos
// visibles en el DTO igual que se ven en las llamadas REST
typealias RepresentantesNotification = Notificacion<RepresentanteDto> // RepresentanteDto
typealias RaquetasNotification = Notificacion<RaquetaDto> // RaquetaDto
typealias TenistasNotification = Notificacion<TenistaDto> // TenistaDto
