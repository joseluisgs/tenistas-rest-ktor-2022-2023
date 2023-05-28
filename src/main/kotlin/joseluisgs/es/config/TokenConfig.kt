package joseluisgs.es.config

import io.ktor.server.config.*
import mu.KotlinLogging
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
data class TokenConfig(
    @InjectedParam private val config: ApplicationConfig = ApplicationConfig("application.conf")
) {
    val audience = config.propertyOrNull("jwt.audience")?.getString() ?: "jwt-audience"
    val secret = config.propertyOrNull("jwt.secret")?.getString() ?: "jwt-secret"
    val issuer = config.propertyOrNull("jwt.issuer")?.getString() ?: "jwt-issuer"
    val realm = config.propertyOrNull("jwt.realm")?.getString() ?: "jwt-realm"
    val expiration = config.propertyOrNull("jwt.expiration")?.getString()?.toLong() ?: 3600L

    init {
        logger.debug { "Iniciando la configuración de Token" }
    }
}