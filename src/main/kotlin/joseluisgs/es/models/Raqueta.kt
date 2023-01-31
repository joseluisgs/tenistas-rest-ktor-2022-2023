package joseluisgs.es.models

import java.time.LocalDateTime
import java.util.*

data class Raqueta(
    // Identificador
    val id: UUID = UUID.randomUUID(),

    // Datos
    val marca: String,
    val precio: Double,

    // Relaciones
    val representanteId: UUID,

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario

)