package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import joseluisgs.es.config.TokenConfig
import joseluisgs.es.services.tokens.TokenException
import joseluisgs.es.services.tokens.TokensService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

// Seguridad en base a JWT
fun Application.configureSecurity() {

    // Inyectamos la configuración de Tokens
    val tokenConfig: TokenConfig = get { parametersOf(environment.config) }

    // Inyectamos el servicio de tokens
    val jwtService: TokensService by inject()

    authentication {
        jwt {
            // Cargamos el verificador con los datos de la configuracion
            verifier(jwtService.verifyJWT())
            // con realm aseguramos la ruta que estamos protegiendo
            realm = tokenConfig.realm
            validate { credential ->
                // Si el token es valido, ademas tiene la udiencia indicada,
                // y tiene el campo del usuario para compararlo con el que nosotros queremos
                // devolvemos el JWTPrincipal, si no devolvemos null
                if (credential.payload.audience.contains(tokenConfig.audience) &&
                    credential.payload.getClaim("username").asString().isNotEmpty()
                )
                    JWTPrincipal(credential.payload)
                else null
            }

            challenge { defaultScheme, realm ->
                // call.respond(HttpStatusCode.Unauthorized, "Token invalido o expirado")
                // Así me lo llevo al status pages plugin!!!
                throw TokenException.InvalidTokenException("Token invalido o expirado")
            }
        }
    }

}
