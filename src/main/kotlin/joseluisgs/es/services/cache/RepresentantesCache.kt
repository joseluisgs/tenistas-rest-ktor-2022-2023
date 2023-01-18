package joseluisgs.es.services.cache

import io.github.reactivecircus.cache4k.Cache
import joseluisgs.es.models.Representante
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*
import kotlin.time.Duration.Companion.minutes

// Configuramos la caché de representantes


private val logger = KotlinLogging.logger {}

@Single
class RepresentantesCache {
    val hasRefreshAllCacheJob: Boolean = false // Si queremos que se refresque el cache
    val refreshTime = 60 * 60 * 1000L // 1 hora en milisegundos

    // Creamos la caché y configuramos a medida
    val cache = Cache.Builder()
        // Si le ponemos opciones de cacheo si no usara las de por defecto
        .maximumCacheSize(100) // Tamaño máximo de la caché si queremos limitarla
        .expireAfterAccess(60.minutes) // Vamos a cachear durante
        .build<UUID, Representante>()
}