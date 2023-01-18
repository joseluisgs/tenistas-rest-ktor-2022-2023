package joseluisgs.es.models

import java.time.LocalDateTime
import java.util.*

data class Representante(
    // Identificador
    val id: UUID = UUID.randomUUID(),

    // Datos
    val nombre: String,
    val email: String,

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
)