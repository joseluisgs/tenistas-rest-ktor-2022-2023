package joseluisgs.es.plugins

import io.ktor.server.application.*
import org.koin.ksp.generated.defaultModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        // Si quiero ver los logs de Koin
        slf4jLogger()
        // Modulos con las dependencias, usamos el default, si no crear modulos
        defaultModule()
    }
}