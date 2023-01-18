package joseluisgs.es.mappers

import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.models.Representante

fun Representante.toDto() = RepresentanteDto(
    id = this.id,
    nombre = this.nombre,
    email = this.email,
    metadata = RepresentanteDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun RepresentanteDto.toModel() = Representante(
    nombre = this.nombre,
    email = this.email
)