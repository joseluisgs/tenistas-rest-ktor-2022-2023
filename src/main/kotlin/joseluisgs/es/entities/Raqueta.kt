package joseluisgs.es.entities

import org.ufoss.kotysa.h2.H2Table
import java.time.LocalDateTime
import java.util.*

object RaquetasTable : H2Table<RaquetaEntity>("raquetas") {
    // Identificador
    val id = uuid(RaquetaEntity::id).primaryKey()

    // Datos
    val marca = varchar(RaquetaEntity::marca, size = 100)
    val precio = doublePrecision(RaquetaEntity::precio)

    // Realcion con el representante
    val representanteId = uuid(RaquetaEntity::representanteId, "representante_id").foreignKey(RepresentantesTable.id)

    // Historicos y metadata
    val createdAt = timestamp(RaquetaEntity::createdAt, "created_at")
    val updatedAt = timestamp(RaquetaEntity::updatedAt, "updated_at")
    val deleted = boolean(RaquetaEntity::deleted)
}

// El DTO de la base de datos
data class RaquetaEntity(
    // Identificador
    val id: UUID = UUID.randomUUID(),

    // Datos
    val marca: String,
    val precio: Double,

    // Realcion con el representante
    val representanteId: UUID, // No permitimos nulos

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
)