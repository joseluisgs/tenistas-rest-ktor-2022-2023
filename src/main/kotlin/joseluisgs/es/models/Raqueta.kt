package joseluisgs.es.models

import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Raqueta(
    // Identificador
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),

    // Datos
    val marca: String,
    val precio: Double,

    // Relaciones
    @Serializable(with = UUIDSerializer::class)
    val represetanteId: UUID? = null,

    // Historicos
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime = LocalDateTime.now()

)