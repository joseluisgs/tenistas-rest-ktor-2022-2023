package joseluisgs.es.config

import io.ktor.server.config.*
import mu.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
data class StorageConfig(
    @InjectedParam private val config: ApplicationConfig = ApplicationConfig("application.conf")
) {
    val uploadDir = config.propertyOrNull("storage.uploadDir")?.getString() ?: "uploads"
    val environment = config.propertyOrNull("ktor.environment")?.getString() ?: "dev"

    init {
        logger.debug { "Iniciando la configuración de Storage" }
    }
}