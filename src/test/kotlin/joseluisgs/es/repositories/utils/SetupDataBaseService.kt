package joseluisgs.es.repositories.utils

import io.ktor.server.config.*
import joseluisgs.es.config.DataBaseConfig
import joseluisgs.es.services.database.DataBaseService

fun getDataBaseService(): DataBaseService {
    val config = ApplicationConfig("application.conf")

    // Leemos la configuración de la base de datos
    val dataBaseConfigParams = mapOf(
        "driver" to config.property("database.driver").getString(),
        "protocol" to config.property("database.protocol").getString(),
        "user" to config.property("database.user").getString(),
        "password" to config.property("database.password").getString(),
        "database" to config.property("database.database").getString(),
        "initDatabaseData" to config.property("database.initDatabaseData").getString(),
    )

    val dataBaseConfig = DataBaseConfig(dataBaseConfigParams)
    val dataBaseService = DataBaseService(dataBaseConfig)
    dataBaseService.initDataBaseService()

    return dataBaseService
}