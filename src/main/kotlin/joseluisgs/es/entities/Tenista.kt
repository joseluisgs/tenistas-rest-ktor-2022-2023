package joseluisgs.es.entities

import org.ufoss.kotysa.h2.H2Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object TenistasTable : H2Table<TenistaEntity>("tenistas") {
    // Identificador
    val id = uuid(TenistaEntity::id).primaryKey()

    // Datos
    val nombre = varchar(TenistaEntity::nombre, size = 100)
    val ranking = integer(TenistaEntity::ranking)
    val fechaNacimiento = date(TenistaEntity::fechaNacimiento, "fecha_nacimiento")
    val añoProfesional = integer(TenistaEntity::añoProfesional, "año_profesional")
    val altura = integer(TenistaEntity::altura)
    val peso = integer(TenistaEntity::peso)
    val manoDominante = varchar(TenistaEntity::manoDominante, "mano_dominante", size = 15)
    val tipoReves = varchar(TenistaEntity::tipoReves, "tipo_reves", size = 15)
    val puntos = integer(TenistaEntity::puntos)
    val pais = varchar(TenistaEntity::pais, size = 100)
    val raquetaId = uuid(TenistaEntity::raquetaId, "raqueta_id", null).foreignKey(RepresentantesTable.id)

    // Historicos y metadata
    val createdAt = timestamp(TenistaEntity::createdAt, "created_at")
    val updatedAt = timestamp(TenistaEntity::updatedAt, "updated_at")
    val deleted = boolean(TenistaEntity::deleted)
}

// El DTO de la base de datos
data class TenistaEntity(
    // Identificador
    val id: UUID,

    // Datos
    var nombre: String,
    var ranking: Int,
    var fechaNacimiento: LocalDate,
    var añoProfesional: Int,
    var altura: Int,
    var peso: Int,
    var manoDominante: String,
    var tipoReves: String,
    var puntos: Int,
    var pais: String,
    var raquetaId: UUID? = null, // No tiene por que tener raqueta

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
)
