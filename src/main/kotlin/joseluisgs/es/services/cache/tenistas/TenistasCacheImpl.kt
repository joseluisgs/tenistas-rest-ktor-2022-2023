package joseluisgs.es.services.cache.tenistas

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.models.Tenista
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*
import kotlin.time.Duration.Companion.minutes

private val logger = KotlinLogging.logger {}

@Single
class TenistasCacheImpl : TenistasCache {
    override val hasRefreshAllCacheJob: Boolean = false // Si queremos que se refresque el cache
    override val refreshTime = 60 * 60 * 1000L // 1 hora en milisegundos

    // Creamos la caché y configuramos a medida
    override val cache = Cache.Builder()
        // Si le ponemos opciones de cacheo si no usara las de por defecto
        .maximumCacheSize(100) // Tamaño máximo de la caché si queremos limitarla
        .expireAfterAccess(60.minutes) // Vamos a cachear durante
        .build<UUID, Tenista>()

    init {
        logger.debug { "Iniciando el sistema de caché de tenistas" }
    }
}