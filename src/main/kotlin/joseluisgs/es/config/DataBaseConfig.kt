package joseluisgs.es.config

import io.ktor.server.config.*
import mu.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
data class DataBaseConfig(
    @InjectedParam private val config: ApplicationConfig = ApplicationConfig("application.conf")
) {
    val driver = config.propertyOrNull("database.driver")?.getString() ?: "h2"
    val protocol = config.propertyOrNull("database.protocol")?.getString() ?: "mem"
    val user = config.propertyOrNull("database.user")?.getString() ?: "sa"
    val password = config.propertyOrNull("database.password")?.getString() ?: ""
    val database = config.propertyOrNull("database.database")?.getString() ?: "r2dbc:h2:mem:///test;DB_CLOSE_DELAY=-1"
    val initDatabaseData = config.propertyOrNull("database.initDatabaseData")?.getString()?.toBoolean() ?: true


    init {
        logger.debug { "Iniciando la configuración de Base de Datos" }
    }

}