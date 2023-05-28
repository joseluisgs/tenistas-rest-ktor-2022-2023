package joseluisgs.es.plugins

import io.ktor.server.application.*
import joseluisgs.es.config.StorageConfig
import joseluisgs.es.services.storage.StorageService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureStorage() {
    // Inyectamos la configuración de Storage
    val storageConfig: StorageConfig = get { parametersOf(environment.config) }
    // Inyectamos el servicio de storage
    val storageService: StorageService by inject()
    // Inicializamos el servicio de storage
    storageService.initStorageDirectory()

}