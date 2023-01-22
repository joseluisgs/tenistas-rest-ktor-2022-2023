package joseluisgs.es.config

import mu.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
data class DataBaseConfig(
    @InjectedParam private val config: Map<String, String>
) {
    val driver = config["driver"].toString()
    val protocol = config["protocol"].toString()
    val user = config["user"].toString()
    val password = config["password"].toString()
    val database = config["database"].toString()
    val initDatabaseData = config["initDatabaseData"]?.toBooleanStrictOrNull() ?: true


    init {
        logger.debug { "Iniciando la configuración de Base de Datos" }
    }

}