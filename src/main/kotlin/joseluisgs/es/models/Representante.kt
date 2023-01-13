package joseluisgs.es.models

import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Representante(
    // Identificador
    val id: Long,
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID = UUID.randomUUID(),

    // Datos
    val nombre: String,
    val email: String,

    // Historicos
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)