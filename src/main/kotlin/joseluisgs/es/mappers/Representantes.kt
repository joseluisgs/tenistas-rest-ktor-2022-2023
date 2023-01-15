package joseluisgs.es.mappers

import joseluisgs.es.dto.RepresentanteDTO
import joseluisgs.es.models.Representante

fun Representante.toDto() = RepresentanteDTO(
    nombre = this.nombre,
    email = this.email
)

fun RepresentanteDTO.toModel() = Representante(
    nombre = this.nombre,
    email = this.email
)