package joseluisgs.es.dto

import joseluisgs.es.models.Representante
import joseluisgs.es.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class RepresentantesPageDTO(
    val page: Int,
    val perPage: Int,
    val data: List<Representante>,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = LocalDateTime.now()
)

@Serializable
data class RepresentanteDTO(
    val nombre: String,
    val email: String
)