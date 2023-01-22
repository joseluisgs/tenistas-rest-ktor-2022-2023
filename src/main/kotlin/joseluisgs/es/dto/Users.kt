package joseluisgs.es.dto

import joseluisgs.es.models.User
import joseluisgs.es.serializers.LocalDateTimeSerializer
import joseluisgs.es.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class UserDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val nombre: String,
    val email: String,
    val username: String,
    val avatar: String,
    val role: User.Role,
    val metadata: MetaData
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

@Serializable
data class UserCreateDto(
    val nombre: String,
    val email: String,
    val username: String,
    val password: String,
    val avatar: String? = null,
    val role: User.Role? = User.Role.USER,
)

@Serializable
data class UserUpdateDto(
    val nombre: String,
    val email: String,
    val username: String,
)

@Serializable
data class UserLoginDto(
    val username: String,
    val password: String
)

@Serializable
data class UserWithTokenDto(
    val user: UserDto,
    val token: String
)