package joseluisgs.es.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*

fun Application.configureCachingHeaders() {
    routing {
        // Definimos una estrategia de cacheo, en este caso global pero puede ser por ruta o llamada
        install(CachingHeaders) {
            options { call, content ->
                when (content.contentType?.withoutParameters()) {
                    ContentType.Text.Plain -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
                    ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 60))
                    // Json y otros
                    ContentType.Any -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 10))
                    else -> null
                }
            }
        }
    }
}