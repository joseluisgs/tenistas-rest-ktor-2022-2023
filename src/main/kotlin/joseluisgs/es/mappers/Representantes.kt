package joseluisgs.es.mappers

import joseluisgs.es.dto.RepresentanteDTO
import joseluisgs.es.models.Representante

fun Representante.toDto() = RepresentanteDTO(
    id = this.id,
    nombre = this.nombre,
    email = this.email,
    metadata = RepresentanteDTO.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
)

fun RepresentanteDTO.toModel() = Representante(
    nombre = this.nombre,
    email = this.email
)