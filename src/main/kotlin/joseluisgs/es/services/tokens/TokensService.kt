package joseluisgs.es.services.tokens

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import joseluisgs.es.config.TokenConfig
import joseluisgs.es.models.User
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.util.*

private val logger = KotlinLogging.logger {}

@Single
class TokensService(
    private val tokenConfig: TokenConfig
) {

    init {
        logger.debug { "Iniciando servicio de tokens con audience: ${tokenConfig.config["audience"]}" }
    }

    fun generateJWTToken(user: User): String {
        return JWT.create()
            .withAudience(tokenConfig.config["audience"])
            .withIssuer(tokenConfig.config["issuer"])
            .withSubject("Authentication")
            // claims de usuario
            .withClaim("username", user.username)
            .withClaim("usermail", user.email)
            .withClaim("userId", user.id.toString())
            .withExpiresAt(
                Date(
                    System.currentTimeMillis() * 1000 // viene en ms y lo paso a segundos
                            + (tokenConfig.config["expiration"]?.toLong() ?: 3600)
                )
            )
            .sign(Algorithm.HMAC512(tokenConfig.config["secret"]))
    }

    fun verifyJWTToken(): JWTVerifier {
        return JWT.require(Algorithm.HMAC512(tokenConfig.config["secret"]))
            .withAudience(tokenConfig.config["audience"])
            .withIssuer(tokenConfig.config["issuer"])
            .build()
    }
}