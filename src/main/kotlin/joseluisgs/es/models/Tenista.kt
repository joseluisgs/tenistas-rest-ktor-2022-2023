package joseluisgs.es.models

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Tenista(
    // Identificador
    val id: UUID = UUID.randomUUID(),

    // Datos
    var nombre: String,
    var ranking: Int,
    var fechaNacimiento: LocalDate,
    var añoProfesional: Int,
    var altura: Int,
    var peso: Int,
    var manoDominante: ManoDominante,
    var tipoReves: TipoReves,
    var puntos: Int,
    var pais: String,
    var raquetaId: UUID? = null, // No tiene por que tener raqueta

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
) {

    // ENUMS de la propia clase
    enum class ManoDominante {
        DERECHA, IZQUIERDA
    }

    enum class TipoReves {
        UNA_MANO, DOS_MANOS
    }


}