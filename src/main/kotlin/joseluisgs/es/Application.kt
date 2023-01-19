package joseluisgs.es

import io.ktor.server.application.*
import io.ktor.server.netty.*
import joseluisgs.es.plugins.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    // Configuramos e iniciamos cada elemento o Plugin que necesitamos
    // OJO con el orden, si no se hace en el orden correcto, no funcionará o dará excepciones
    // El pensamiento es lógico, porque unos influyen en el resto

    // El primero es Koin, para que tenga poder inyectar dependencias del resto de cosas que necesitamos
    configureKoin()

    // Configuramos el almacenamiento
    configureStorage()

    // Configuramos Middleware la seguridad con JWT, debe ir antes que el resto de plugins que trabajen con rutas
    configureSecurity()

    // // Configuramos WebSockets, ideal para chat o notificaciones en tiempo real. Debe ir antes que las rutas http.
    configureWebSockets()

    // Principales que debería tener tu api rest!!!

    // Configuramos la serialización
    configureSerialization()

    // Configuramos las rutas
    configureRouting()

    // Configuramos la validación de body en requests, puedes hacerlo a mano, pero es más cómodo con este plugin
    configureValidation()

    // Opcionales segun el problema interesantes para el desarrollo

    // Configuramos el CORS, fudamentales si tenemos origenes cruzados
    configureCors()

    // Configuramos los headers de cacheo,
    configureCachingHeaders()

    // Configuramos el compreso de gzip y otros
    configureCompression()

    // Otros...

}
