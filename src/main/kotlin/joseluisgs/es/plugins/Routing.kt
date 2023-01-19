package joseluisgs.es.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.routes.*

// Configuramos las rutas con esta función de extensión
// Podemos definirlas en un fichero aparte o dentro
fun Application.configureRouting() {

    routing {
        // Defínelas por orden de prioridad y sin que se solapen

        // Ruta raíz
        get("/") {
            call.respondText("Tenistas API REST Ktor. 2º DAM")
        }

        // podriamos añadir el resto de rutas aqui de la misma forma
        // get("/tenistas") {

        // Pero vamos a crear un fichero de rutas para ello
    }

    // Definidas dentro del paquete de rutas: routes
    webRoutes() // Rutas web /web
    // Intenta ponerlas por orden de importancia y acceso
    representantesRoutes() // Rutas de api /rest/representantes
    usersRoutes() // Rutas de api /rest/users
    storageRoutes() // Rutas de api /rest/storage
    testRoutes() // Rutas de api /rest/test
}
