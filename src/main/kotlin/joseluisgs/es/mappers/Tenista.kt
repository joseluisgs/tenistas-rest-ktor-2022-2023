package joseluisgs.es.mappers

import joseluisgs.es.dto.TenistaCreateDto
import joseluisgs.es.dto.TenistaDto
import joseluisgs.es.entities.TenistaEntity
import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Tenista

fun Tenista.toDto(raqueta: Raqueta?) = TenistaDto(
    id = this.id,
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = this.fechaNacimiento,
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = this.manoDominante,
    tipoReves = this.tipoReves,
    puntos = this.puntos,
    pais = this.pais,
    raqueta = raqueta?.toTenistaDto(),
    metadata = TenistaDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun TenistaCreateDto.toModel() = Tenista(
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = this.fechaNacimiento,
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = this.manoDominante ?: Tenista.ManoDominante.DERECHA,
    tipoReves = this.tipoReves ?: Tenista.TipoReves.DOS_MANOS,
    puntos = this.puntos,
    pais = this.pais,
    raquetaId = this.raquetaId,
)

fun Tenista.toEntity() = TenistaEntity(
    id = this.id,
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = this.fechaNacimiento,
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = this.manoDominante.name,
    tipoReves = this.tipoReves.name,
    puntos = this.puntos,
    pais = this.pais,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

fun TenistaEntity.toModel() = Tenista(
    id = this.id,
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = this.fechaNacimiento,
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = Tenista.ManoDominante.valueOf(this.manoDominante),
    tipoReves = Tenista.TipoReves.valueOf(this.tipoReves),
    puntos = this.puntos,
    pais = this.pais,
    raquetaId = this.raquetaId,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deleted = this.deleted
)

