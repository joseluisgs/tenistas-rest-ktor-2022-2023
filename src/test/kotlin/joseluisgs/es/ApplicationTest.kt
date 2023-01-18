package joseluisgs.es

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    // Cargamos la configuración del entorno
    // La configuración de entorno se encuentra en el fichero application.conf
    // del directorio resources (si hay una propia estará en test/resources)
    // Podemos sobreescribir la configuración de entorno en el test para hacerla más rápida
    private val config = ApplicationConfig("application.conf")


    @Test
    fun trueIsTrue() {
        assertTrue(true)
    }

    @Test
    fun testRoot() = testApplication {
        // Configuramos el entorno de test
        environment { config }

        // Lanzamos la consulta
        val response = client.get("/")

        // Comprobamos que la respuesta y el contenido es correcto
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Tenistas API REST Ktor. 2º DAM", response.bodyAsText())
    }
}
