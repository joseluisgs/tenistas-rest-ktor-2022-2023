package joseluisgs.es.plugins

import io.ktor.server.application.*
import joseluisgs.es.config.DataBaseConfig
import joseluisgs.es.services.database.DataBaseService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureDataBase() {
    // Inyectamos la configuración de DataBase
    val config: DataBaseConfig = get { parametersOf(environment.config) }
    // Inyectamos el servicio de bases de datos
    val dataBaseService: DataBaseService by inject()
    // Inicializamos el servicio de storage
    dataBaseService.initDataBaseService()
}