package joseluisgs.es

import io.ktor.server.application.*
import io.ktor.server.netty.*
import joseluisgs.es.plugins.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRouting()
}
