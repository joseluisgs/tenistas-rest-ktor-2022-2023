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

sealed class TokenException(message: String) : RuntimeException(message) {
    class InvalidTokenException(message: String) : TokenException(message)
    class ExpiredTokenException(message: String) : TokenException(message)
}

@Single
class TokensService(
    private val tokenConfig: TokenConfig
) {

    init {
        logger.debug { "Iniciando servicio de tokens con audience: ${tokenConfig.audience}" }
    }

    fun generateJWT(user: User): String {
        return JWT.create()
            .withAudience(tokenConfig.audience)
            .withIssuer(tokenConfig.issuer)
            .withSubject("Authentication")
            // claims de usuario
            .withClaim("username", user.username)
            .withClaim("usermail", user.email)
            .withClaim("userId", user.id.toString())
            // claims de tiempo de expiración milisegundos desde 1970 + (tiempo en segundos) * 1000 (milisegundos)
            .withExpiresAt(
                Date(System.currentTimeMillis() + tokenConfig.expiration * 1000L)
            )
            .sign(Algorithm.HMAC512(tokenConfig.secret))
    }

    fun verifyJWT(): JWTVerifier {

        return try {
            JWT.require(Algorithm.HMAC512(tokenConfig.secret))
                .withAudience(tokenConfig.audience)
                .withIssuer(tokenConfig.issuer)
                .build()
        } catch (e: Exception) {
            throw TokenException.InvalidTokenException("Token no válido")
        }
    }
}