package joseluisgs.es.repositories.tenistas

import joseluisgs.es.entities.TenistasTable
import joseluisgs.es.mappers.toEntity
import joseluisgs.es.mappers.toModel
import joseluisgs.es.models.Tenista
import joseluisgs.es.services.database.DataBaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
@Named("TenistasRepository")
class TenistasRepositoryImpl(
    private val dataBaseService: DataBaseService
) : TenistasRepository {

    init {
        logger.debug { "Iniciando Repositorio de Tenistas" }
    }

    override suspend fun findAll(): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando los tenistas" }

        return@withContext (dataBaseService.client selectFrom TenistasTable
                orderByAsc TenistasTable.ranking)
            .fetchAll()
            .map { it.toModel() }
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todos los tenistas con página: $page y cantidad: $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        // Ojo que están ordenados por ranking
        return@withContext (dataBaseService.client selectFrom TenistasTable
                orderByAsc TenistasTable.ranking
                limit myLimit offset myOffset)
            .fetchAll()
            .map { it.toModel() }

    }

    override suspend fun findById(id: UUID): Tenista? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando tenista con id: $id" }

        // Buscamos
        return@withContext (dataBaseService.client selectFrom TenistasTable
                where TenistasTable.id eq id
                ).fetchFirstOrNull()?.toModel()
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.debug { "findByMarca: Buscando tenista con nombre: $nombre" }

        return@withContext (dataBaseService.client selectFrom TenistasTable
                where TenistasTable.nombre eq nombre
                ).fetchAll().map { it.toModel() }
    }

    override suspend fun findByRanking(ranking: Int): Tenista? = withContext(Dispatchers.IO) {
        logger.debug { "findByRanking: Buscando tenista con ranking: $ranking" }

        // Buscamos
        return@withContext (dataBaseService.client selectFrom TenistasTable
                where TenistasTable.ranking eq ranking
                ).fetchFirstOrNull()?.toModel()
    }

    override suspend fun save(entity: Tenista): Tenista = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando tenista: $entity" }

        return@withContext (dataBaseService.client insertAndReturn entity.toEntity())
            .toModel()

    }

    override suspend fun update(id: UUID, entity: Tenista): Tenista? = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando tenista: $entity" }

        entity.let {
            val updateEntity = entity.toEntity()

            val res = (dataBaseService.client update TenistasTable
                    set TenistasTable.nombre eq updateEntity.nombre
                    set TenistasTable.ranking eq updateEntity.ranking
                    set TenistasTable.fechaNacimiento eq updateEntity.fechaNacimiento
                    set TenistasTable.añoProfesional eq updateEntity.añoProfesional
                    set TenistasTable.altura eq updateEntity.altura
                    set TenistasTable.peso eq updateEntity.peso
                    set TenistasTable.manoDominante eq updateEntity.manoDominante
                    set TenistasTable.tipoReves eq updateEntity.tipoReves
                    set TenistasTable.puntos eq updateEntity.puntos
                    set TenistasTable.pais eq updateEntity.pais
                    where TenistasTable.id eq id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }

    }

    override suspend fun delete(entity: Tenista): Tenista? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Guardando tenista: ${entity.id}" }

        // Buscamos
        entity.let {
            val res = (dataBaseService.client deleteFrom TenistasTable
                    where TenistasTable.id eq it.id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }
    }
}