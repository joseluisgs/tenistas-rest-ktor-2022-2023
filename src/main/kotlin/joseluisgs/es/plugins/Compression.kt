package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*


fun Application.configureCompression() {
    // Definimos una estrategia de compresión de contenido
    install(Compression) {
        gzip {
            // El tamaño mínimo para empezar a comprimir, podemos fijar un tamaño en bytes
            // y otras opciones
            minimumSize(1024)
        }
    }
}