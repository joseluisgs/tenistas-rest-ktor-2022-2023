package joseluisgs.es.entities

import org.ufoss.kotysa.h2.H2Table
import java.time.LocalDateTime
import java.util.*

/**
 * Objeto que representa la estructura relacional de [Representante]
 */
object RepresentantesTable : H2Table<RepresentanteEntity>("representantes") {
    // Identificador
    val id = uuid(RepresentanteEntity::id).primaryKey()

    // Datos
    val nombre = varchar(RepresentanteEntity::nombre, size = 100)
    val email = varchar(RepresentanteEntity::email, size = 100)

    // Historicos y metadata
    val createdAt = timestamp(RepresentanteEntity::createdAt, "created_at")
    val updatedAt = timestamp(RepresentanteEntity::updatedAt, "updated_at")
    val deleted = boolean(RepresentanteEntity::deleted)
}

/**
 * Entidad que representa una fila de [Representante]
 * @see RepresentantesTable
 */
data class RepresentanteEntity(
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