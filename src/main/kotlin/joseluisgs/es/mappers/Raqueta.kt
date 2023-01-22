package joseluisgs.es.mappers

import joseluisgs.es.dto.RaquetaCreateDto
import joseluisgs.es.dto.RaquetaDto
import joseluisgs.es.entities.RaquetaEntity
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante

fun Raqueta.toDto(representante: Representante) = RaquetaDto(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    represetante = representante.toDto(),
    metadata = RaquetaDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun RaquetaCreateDto.toModel() = Raqueta(
    marca = this.marca,
    precio = this.precio,
    represetanteId = this.representanteId,
)

fun Raqueta.toEntity() = RaquetaEntity(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    representanteId = this.represetanteId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

fun RaquetaEntity.toModel() = Raqueta(
    id = this.id,
    marca = this.marca,
    precio = this.precio,
    represetanteId = this.representanteId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

