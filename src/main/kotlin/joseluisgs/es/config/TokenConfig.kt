package joseluisgs.es.config

import mu.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
data class TokenConfig(
    @InjectedParam val config: Map<String, String>
) {
    init {
        logger.debug { "Iniciando la configuración de Token" }
    }
}