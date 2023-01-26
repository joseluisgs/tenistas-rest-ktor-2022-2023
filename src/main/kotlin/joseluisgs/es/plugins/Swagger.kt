package joseluisgs.es.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.ktor.server.application.*

fun Application.configureSwagger() {
    // Metodos oficiales de Ktor Team
    // Solo OpenAPI
    /*routing {
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }*/
    // OpenAPI y SwaggerUI
    /*routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }*/


    // https://github.com/SMILEY4/ktor-swagger-ui/wiki/Configuration
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = false
        }
        info {
            title = "Ktor Tenistas API REST"
            version = "latest"
            description = "Ejemplo de una API Rest usando Ktor y tecnologías Kotlin."
            contact {
                name = "Jose Luis González Sánchez"
                url = "https://github.com/joseluisgs"
            }
            license {
                name = "Creative Commons Attribution-ShareAlike 4.0 International License"
                url = "https://joseluisgs.dev/docs/license/"
            }
        }
        server {
            url = environment.config.property("server.baseUrl").getString()
            description = "Servidor de la API Rest usando Ktor y tecnologías Kotlin."
        }

        schemasInComponentSection = true
        examplesInComponentSection = true
        automaticTagGenerator = { url -> url.firstOrNull() }
        // Filtramos las rutas que queremos documentar
        // Y los métodos
        // Si lo queremos todo, no hace falta filtrar nada
        pathFilter = { method, url ->
            url.contains("test")
            // Habiltamos el GET para todas y completo para test
            //(method == HttpMethod.Get && url.firstOrNull() == "api")
            // || url.contains("test")
        }

        securityScheme("JWT-Auth") {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            bearerFormat = "jwt"
        }
    }
}
