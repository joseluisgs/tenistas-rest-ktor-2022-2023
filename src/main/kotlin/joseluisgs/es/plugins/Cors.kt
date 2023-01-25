package joseluisgs.es.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors() {
    install(CORS) {
        anyHost() // Permite cualquier host
        allowHeader(HttpHeaders.ContentType) // Permite el header Content-Type
        allowHeader(HttpHeaders.Authorization)
        // allowHost("client-host") // Allow requests from client-host

        // Podemos indicar qué host queremos permitir
        /*allowHost("client-host") // Allow requests from client-host
        allowHost("client-host:8081") // Allow requests from client-host on port 8081
        allowHost(
            "client-host",
            subDomains = listOf("en", "de", "es")
        ) // Allow requests from client-host on subdomains en, de and es
        allowHost("client-host", schemes = listOf("http", "https")) // Allow requests from client-host on http and https

        // o sobre qué métodos queremos permitir
        allowMethod(HttpMethod.Put) // Allow PUT method
        allowMethod(HttpMethod.Delete)  // Allow DELETE method*/
    }
}