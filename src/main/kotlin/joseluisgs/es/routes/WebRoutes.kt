package joseluisgs.es.routes

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

// Vamos a crear una ruta web, para ello usamos una función de extensión de la clase Router
// La llamamos webContent y le decimos el contenido que queremos que se muestre
fun Application.webRoutes() {

    routing {
        // Contenido estático, desde la carpeta resources cuando entran a /web
        static {
            // Si nos preguntan por /web desde la raíz, le mandamos el contenido estático.
            // Tambiín aplicamos redireccion
            resource("/web", "web/index.html")
            resource("*", "web/index.html")
            // todo contenido estático con web/, lo busca en la carpeta web
            static("web") {
                resources("web")
            }
        }
    }
}
