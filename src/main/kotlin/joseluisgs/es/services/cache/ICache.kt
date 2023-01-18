package joseluisgs.es.services.cache

import io.github.reactivecircus.cache4k.Cache

interface ICache<ID : Any, T : Any> {
    val hasRefreshAllCacheJob: Boolean // Si queremos que se refresque el cache con todos los datos
    val refreshTime: Long // tiempo de refresco de todos los datos
    val cache: Cache<ID, T> // La caché
}