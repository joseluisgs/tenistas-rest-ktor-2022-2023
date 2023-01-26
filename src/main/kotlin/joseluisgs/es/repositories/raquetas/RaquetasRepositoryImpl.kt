package joseluisgs.es.repositories.raquetas

import joseluisgs.es.entities.RaquetasTable
import joseluisgs.es.exceptions.DataBaseIntegrityViolationException
import joseluisgs.es.mappers.toEntity
import joseluisgs.es.mappers.toModel
import joseluisgs.es.models.Raqueta
import joseluisgs.es.services.database.DataBaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}


@Single
@Named("RaquetasRepository")
class RaquetasRepositoryImpl(
    private val dataBaseService: DataBaseService
) : RaquetasRepository {

    init {
        logger.debug { "Iniciando Repositorio de Raquetas" }
    }

    override suspend fun findAll(): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todas las raquetas" }

        return@withContext (dataBaseService.client selectFrom RaquetasTable)
            .fetchAll()
            .map { it.toModel() }
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todas las raquetas con página: $page y cantidad: $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        return@withContext (dataBaseService.client selectFrom RaquetasTable limit myLimit offset myOffset)
            .fetchAll()
            .map { it.toModel() }

    }

    override suspend fun findById(id: UUID): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando raqueta con id: $id" }

        // Buscamos
        return@withContext (dataBaseService.client selectFrom RaquetasTable
                where RaquetasTable.id eq id
                ).fetchFirstOrNull()?.toModel()
    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.debug { "findByMarca: Buscando raqueta con marca: $marca" }

        return@withContext (dataBaseService.client selectFrom RaquetasTable).fetchAll()
            .filter { it.marca.lowercase().contains(marca.lowercase()) }
            .map { it.toModel() }
    }

    override suspend fun save(entity: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando raqueta: $entity" }

        return@withContext (dataBaseService.client insertAndReturn entity.toEntity())
            .toModel()

    }

    override suspend fun update(id: UUID, entity: Raqueta): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando raqueta: $entity" }

        entity.let {
            val updateEntity = entity.toEntity()

            val res = (dataBaseService.client update RaquetasTable
                    set RaquetasTable.marca eq updateEntity.marca
                    set RaquetasTable.precio eq updateEntity.precio
                    set RaquetasTable.representanteId eq updateEntity.representanteId
                    where RaquetasTable.id eq id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }

    }

    override suspend fun delete(entity: Raqueta): Raqueta? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Guardando raqueta: ${entity.id}" }

        // Buscamos
        entity.let {
            // meto el try catch para que no una raqueta puede tener tenistas y no se pueda borrar
            try {
                val res = (dataBaseService.client deleteFrom RaquetasTable
                        where RaquetasTable.id eq it.id)
                    .execute()

                if (res > 0) {
                    return@withContext entity
                } else {
                    return@withContext null
                }
            } catch (e: Exception) {
                throw DataBaseIntegrityViolationException()
            }
        }
    }
}