package joseluisgs.es.plugins

import io.ktor.server.application.*
import joseluisgs.es.config.StorageConfig
import joseluisgs.es.services.storage.StorageService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureStorage() {
    // Leemos la configuración de storage de nuestro fichero de configuración
    val storageConfigParams = mapOf<String, String>(
        "baseUrl" to environment.config.property("server.baseUrl").getString(),
        "secureUrl" to environment.config.property("server.baseSecureUrl").getString(),
        "environment" to environment.config.property("ktor.environment").getString(),
        "uploadDir" to environment.config.property("storage.uploadDir").getString()
    )

    // Inyectamos la configuración de Storage
    val storageConfig: StorageConfig = get { parametersOf(storageConfigParams) }
    // Inyectamos el servicio de tokens
    val storageService: StorageService by inject()
    // Inicializamos el servicio de storage
    storageService.initStorageDirectory()

}