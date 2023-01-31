package joseluisgs.es.mappers

import joseluisgs.es.dto.RaquetaCreateDto
import joseluisgs.es.dto.RaquetaDto
import joseluisgs.es.dto.RaquetaTenistaDto
import joseluisgs.es.entities.RaquetaEntity
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante

fun Raqueta.toDto(representante: Representante) = RaquetaDto(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    representante = representante.toDto(),
    metadata = RaquetaDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun Raqueta.toTenistaDto() = RaquetaTenistaDto(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    representanteId = this.representanteId,
    metadata = RaquetaTenistaDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun RaquetaCreateDto.toModel() = Raqueta(
    marca = this.marca,
    precio = this.precio,
    representanteId = this.representanteId,
)

fun Raqueta.toEntity() = RaquetaEntity(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    representanteId = this.representanteId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

fun RaquetaEntity.toModel() = Raqueta(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    representanteId = this.representanteId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

