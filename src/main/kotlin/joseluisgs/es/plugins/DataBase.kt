package joseluisgs.es.plugins

import io.ktor.server.application.*
import joseluisgs.es.config.DataBaseConfig
import joseluisgs.es.services.database.DataBaseService
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureDataBase() {
    // Leemos la configuración de storage de nuestro fichero de configuración
    val dataBaseConfigParams = mapOf(
        "driver" to environment.config.property("database.driver").getString(),
        "protocol" to environment.config.property("database.protocol").getString(),
        "user" to environment.config.property("database.user").getString(),
        "password" to environment.config.property("database.password").getString(),
        "database" to environment.config.property("database.database").getString(),
        "initDatabaseData" to environment.config.property("database.initDatabaseData").getString(),
    )

    // Inyectamos la configuración de DataBase
    val dataBaseConfig: DataBaseConfig = get { parametersOf(dataBaseConfigParams) }
    // Inyectamos el servicio de bases de datos
    val dataBaseService: DataBaseService by inject()
    // Inicializamos el servicio de storage
    dataBaseService.initDataBaseService()

}