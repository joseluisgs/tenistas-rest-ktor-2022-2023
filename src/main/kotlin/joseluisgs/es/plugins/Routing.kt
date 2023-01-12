package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.routes.testRoutes
import joseluisgs.es.routes.webRoutes

// Configuramos las rutas con esta función de extensión
// Podemos definirlas en un fichero aparte o dentro
fun Application.configureRouting() {

    routing {
        // Ruta raíz
        get("/") {
            call.respondText("Tenistas API REST Ktor. 2º DAM")
        }

        // podriamos añadir el resto de rutas aqui de la misma forma
        // get("/tenistas") {

        // Pero vamos a crear un fichero de rutas para ello
    }

    // Definidas dentro del paquete de rutas: routes
    webRoutes() // Rutas web
    testRoutes() // Rutas de test
}
