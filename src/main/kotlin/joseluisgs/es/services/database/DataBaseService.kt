package joseluisgs.es.services.database

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import joseluisgs.es.config.DataBaseConfig
import joseluisgs.es.db.getRaquetasInit
import joseluisgs.es.db.getRepresentantesInit
import joseluisgs.es.db.getTenistasInit
import joseluisgs.es.db.getUsuariosInit
import joseluisgs.es.entities.RaquetasTable
import joseluisgs.es.entities.RepresentantesTable
import joseluisgs.es.entities.TenistasTable
import joseluisgs.es.entities.UsersTable
import joseluisgs.es.mappers.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.koin.core.annotation.Single
import org.ufoss.kotysa.H2Tables
import org.ufoss.kotysa.r2dbc.sqlClient
import org.ufoss.kotysa.tables

private val logger = KotlinLogging.logger {}

@Single
class DataBaseService(
    private val dataBaseConfig: DataBaseConfig
) {


    private val connectionOptions = ConnectionFactoryOptions.builder()
        .option(ConnectionFactoryOptions.DRIVER, dataBaseConfig.driver)
        .option(ConnectionFactoryOptions.PROTOCOL, dataBaseConfig.protocol)  // file, mem
        .option(ConnectionFactoryOptions.USER, dataBaseConfig.user)
        .option(ConnectionFactoryOptions.PASSWORD, dataBaseConfig.password)
        .option(ConnectionFactoryOptions.DATABASE, dataBaseConfig.database)
        .build()

    val client = ConnectionFactories
        .get(connectionOptions)
        .sqlClient(getTables())

    private val initData get() = dataBaseConfig.initDatabaseData


    fun initDataBaseService() {
        logger.debug { "Inicializando servicio de Bases de Datos: ${dataBaseConfig.database}" }

        // creamos las tablas
        createTables()

        // Inicializamos los datos de la base de datos
        if (initData) {
            logger.debug { "Inicializando datos de la base de datos" }
            clearDataBaseData()
            initDataBaseData()
        }
    }

    private fun getTables(): H2Tables {
        // Creamos un objeto H2Tables con las tablas de la base de datos
        // Entidades de la base de datos
        return tables()
            .h2(
                UsersTable,
                RepresentantesTable,
                RaquetasTable,
                TenistasTable
            )
    }

    private fun createTables() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO)
        logger.debug { "Creando tablas de la base de datos" }
        // Creamos las tablas
        scope.launch {
            client createTableIfNotExists UsersTable
            client createTableIfNotExists RepresentantesTable
            client createTableIfNotExists RaquetasTable
            client createTableIfNotExists TenistasTable
        }
    }

    fun clearDataBaseData() = runBlocking {
        // Primero borramos los datos evitando la cascada
        logger.debug { "Borrando datos..." }
        try {
            client deleteAllFrom TenistasTable
            client deleteAllFrom RaquetasTable
            client deleteAllFrom RepresentantesTable
            client deleteAllFrom UsersTable
        } catch (_: Exception) {

        }
    }

    fun initDataBaseData() = runBlocking {

        // Creamos los datos
        logger.debug { "Creando datos..." }

        // Seguimos el orden de las tablas

        logger.debug { "Creando usuarios..." }
        getUsuariosInit().forEach {
            client insert it.toEntity()
        }

        logger.debug { "Creando representantes..." }
        getRepresentantesInit().forEach {
            client insert it.toEntity()
        }

        logger.debug { "Creando raquetas..." }
        getRaquetasInit().forEach {
            client insert it.toEntity()
        }

        logger.debug { "Creando tenistas..." }
        getTenistasInit().forEach {
            client insert it.toEntity()
        }
    }

}