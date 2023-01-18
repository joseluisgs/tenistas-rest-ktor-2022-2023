package joseluisgs.es.models

import java.time.LocalDateTime
import java.util.*

data class User(
    // Identificador
    val id: UUID = UUID.randomUUID(),
    val nombre: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String,
    val role: Role = Role.USER,

    // Historicos y metadata
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
) {
    enum class Role {
        USER, ADMIN
    }
}