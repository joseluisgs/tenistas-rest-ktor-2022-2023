package joseluisgs.es.dto

import joseluisgs.es.models.Tenista
import joseluisgs.es.serializers.LocalDateSerializer
import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Serializable
data class TenistasPageDto(
    val page: Int,
    val perPage: Int,
    val data: List<TenistaDto>,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = LocalDateTime.now()
)

@Serializable
data class TenistaCreateDto(
    val nombre: String,
    val ranking: Int,
    @Serializable(with = LocalDateSerializer::class)
    val fechaNacimiento: LocalDate,
    val añoProfesional: Int,
    val altura: Int,
    val peso: Int,
    val manoDominante: Tenista.ManoDominante? = Tenista.ManoDominante.DERECHA,
    val tipoReves: Tenista.TipoReves? = Tenista.TipoReves.DOS_MANOS,
    val puntos: Int,
    val pais: String,
    @Serializable(with = UUIDSerializer::class)
    val raquetaId: UUID? = null,
)

@Serializable
data class TenistaDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val nombre: String,
    val ranking: Int,
    @Serializable(with = LocalDateSerializer::class)
    val fechaNacimiento: LocalDate,
    val añoProfesional: Int,
    val altura: Int,
    val peso: Int,
    val manoDominante: Tenista.ManoDominante,
    val tipoReves: Tenista.TipoReves,
    val puntos: Int,
    val pais: String,
    val raqueta: RaquetaTenistaDto?,
    val metadata: MetaData? = null,
) {
    @Serializable
    data class MetaData(
        @Serializable(with = LocalDateTimeSerializer::class)
        val createdAt: LocalDateTime? = LocalDateTime.now(),
        @Serializable(with = LocalDateTimeSerializer::class)
        val updatedAt: LocalDateTime? = LocalDateTime.now(),
        val deleted: Boolean = false
    )
}
