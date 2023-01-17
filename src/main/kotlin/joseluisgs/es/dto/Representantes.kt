package joseluisgs.es.dto

import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class RepresentantesPageDto(
    val page: Int,
    val perPage: Int,
    val data: List<RepresentanteDto>,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = LocalDateTime.now()
)

@Serializable
data class RepresentanteDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val nombre: String,
    val email: String,
    val metadata: MetaData? = null,
) {
    @Serializable
    class MetaData(
        @Serializable(with = LocalDateTimeSerializer::class)
        val createdAt: LocalDateTime? = LocalDateTime.now(),
        @Serializable(with = LocalDateTimeSerializer::class)
        val updatedAt: LocalDateTime? = LocalDateTime.now(),
        val deleted: Boolean = false
    )
}