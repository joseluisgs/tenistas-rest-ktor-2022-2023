package joseluisgs.es

import io.ktor.server.application.*
import io.ktor.server.netty.*
import joseluisgs.es.plugins.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    logger.info { "Starting Ktor Application" }
    // logger.debug { environment.config.property("ktor.environmet").getString() }


    // Configuramos e iniciamos cada elemento o Plugin que necesitamos

    // El primero es Koin, para que tenga todo cargado para inyectar
    configureKoin()
    // Debe ir antes que las rutas, si no no excepcion
    configureWebSockets() // Configuramos WebSockets, ideal para chat o notificaciones en tiempo real

    // Principales!!!
    configureSerialization() // Configuramos la serialización
    configureRouting() // Configuramos las rutas
    configureValidation() // Configuramos la validación de body en requests, puedes hacerlo a mano

    // Otros plugins
    // configureSecurity() // Configuramos la seguridad, ideal para JWT
    // configureWebSockets() // Configuramos WebSockets, ideal para chat o notificaciones en tiempo real

    // opcionales segun el problema interesantes para el desarrollo
    configureCachingHeaders() // Configuramos los headers de cacheo,
    configureCompression() // Configuramos el compreso de gzip y otros
    configureCors() // Configuramos el CORS, sobre todo para web,
}
