package joseluisgs.es.repositories.representantes

import joseluisgs.es.entities.RepresentantesTable
import joseluisgs.es.exceptions.DataBaseIntegrityViolationException
import joseluisgs.es.mappers.toEntity
import joseluisgs.es.mappers.toModel
import joseluisgs.es.models.Representante
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
@Named("PersonasRepository")
class RepresentantesRepositoryImpl(
    private val dataBaseService: DataBaseService
) : RepresentantesRepository {


    init {
        logger.debug { "Iniciando Repositorio de Representantes" }
    }

    override suspend fun findAll(): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findAll: Buscando todos los representantes" }

        return@withContext (dataBaseService.client selectFrom RepresentantesTable)
            .fetchAll()
            .map { it.toModel() }
    }

    override suspend fun findAllPageable(page: Int, perPage: Int): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findAllPageable: Buscando todos los representantes con página: $page y cantidad: $perPage" }

        val myLimit = if (perPage > 100) 100L else perPage.toLong()
        val myOffset = (page * perPage).toLong()

        return@withContext (dataBaseService.client selectFrom RepresentantesTable limit myLimit offset myOffset)
            .fetchAll()
            .map { it.toModel() }
    }

    override suspend fun findById(id: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "findById: Buscando representante con id: $id" }

        // Buscamos
        return@withContext (dataBaseService.client selectFrom RepresentantesTable
                where RepresentantesTable.id eq id
                ).fetchFirstOrNull()?.toModel()
    }


    override suspend fun findByNombre(nombre: String): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.debug { "findByNombre: Buscando representante con nombre: $nombre" }

        return@withContext (dataBaseService.client selectFrom RepresentantesTable
                where RepresentantesTable.nombre eq nombre
                ).fetchAll().map { it.toModel() }
    }


    override suspend fun save(entity: Representante): Representante = withContext(Dispatchers.IO) {
        logger.debug { "save: Guardando representante: $entity" }

        return@withContext (dataBaseService.client insertAndReturn entity.toEntity())
            .toModel()
    }

    override suspend fun update(id: UUID, entity: Representante): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "update: Actualizando representante: $entity" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Actualizamos los datos
        entity.let {
            val updateEntity = entity.toEntity()

            val res = (dataBaseService.client update RepresentantesTable
                    set RepresentantesTable.nombre eq updateEntity.nombre
                    set RepresentantesTable.email eq updateEntity.email
                    where RepresentantesTable.id eq id)
                .execute()

            if (res > 0) {
                return@withContext entity
            } else {
                return@withContext null
            }
        }
    }

    override suspend fun delete(entity: Representante): Representante? = withContext(Dispatchers.IO) {
        logger.debug { "delete: Borrando representante con id: ${entity.id}" }

        // Buscamos
        // val representante = findById(id) // no va a ser null por que lo filtro en la cache
        // Borramos
        entity.let {
            // meto el try catch para que no se caiga la aplicación si no se puede borrar por tener raquetas asociadas
            try {
                val res = (dataBaseService.client deleteFrom RepresentantesTable
                        where RepresentantesTable.id eq it.id)
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