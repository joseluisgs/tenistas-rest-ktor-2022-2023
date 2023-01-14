package joseluisgs.es.repositories.representantes

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.models.Representante
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

private val logger = KotlinLogging.logger {}

class RepresentantesCachedRepositoryImpl(
    private val repository: RepresentantesRepository, // Repositorio de datos originales
) : RepresentantesRepository {
    @OptIn(ExperimentalTime::class)
    private val cache = Cache.Builder()
        .expireAfterAccess(24.hours) // Vamos a cachear durante 24 horas
        .build<Long, Representante>()


    init {
        logger.debug { "Iniciando Repositorio de cache de Representantes" }
    }


    override fun findAllPageable(page: Int, perPage: Int): Flow<Representante> {
        TODO("Not yet implemented")
    }

    override suspend fun findByUuid(uuid: UUID): Representante? {
        TODO("Not yet implemented")
    }

    override fun findByNombre(nombre: String): Flow<Representante> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Flow<Representante> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Representante? {
        TODO("Not yet implemented")
    }

    override suspend fun save(entity: Representante): Representante {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: Long, entity: Representante): Representante {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long): Representante {
        TODO("Not yet implemented")
    }
}