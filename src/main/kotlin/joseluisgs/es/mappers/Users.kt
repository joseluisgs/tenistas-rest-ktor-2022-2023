package joseluisgs.es.mappers

import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserDto
import joseluisgs.es.entities.UserEntity
import joseluisgs.es.models.User

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        avatar = this.avatar,
        role = this.role,
        metadata = UserDto.MetaData(
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            deleted = this.deleted
        )
    )
}

fun UserCreateDto.toModel(): User {
    return User(
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar,
        role = this.role
    )
}

fun UserEntity.toModel(): User {
    return User(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar,
        role = User.Role.valueOf(this.role),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar,
        role = this.role.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted
    )
}




