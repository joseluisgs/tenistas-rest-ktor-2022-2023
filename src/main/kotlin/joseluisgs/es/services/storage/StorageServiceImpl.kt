package joseluisgs.es.services.storage

import joseluisgs.es.config.StorageConfig
import joseluisgs.es.exceptions.FileNotSaveException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.io.File
import java.io.FileNotFoundException

private val logger = KotlinLogging.logger {}

@Single
class StorageServiceImpl(
    private val storageConfig: StorageConfig
) : StorageService {

    init {
        logger.debug { "Iniciando servicio de almacenamiento en: ${storageConfig.uploadDir}" }
    }

    override fun initStorageDirectory() {
        logger.debug { "Iniciando el directorio de almacenamiento en: ${storageConfig.uploadDir}" }
        if (!File(storageConfig.uploadDir).exists()) {
            logger.debug { "Creando el directorio de almacenamiento en: ${storageConfig.uploadDir}" }
            File(storageConfig.uploadDir).mkdir()
        } else {
            // Si existe, borramos todos los ficheros // solo en dev
            if (storageConfig.environment == "dev") {
                logger.debug { "Modo de desarrollo. Borrando el contenido del almacenamiento" }
                File(storageConfig.uploadDir).listFiles()?.forEach { it.delete() }
            }
        }
    }

    override suspend fun saveFile(pathName: String, fileName: String, fileBytes: ByteArray): Map<String, String> =
        withContext(Dispatchers.IO) {
            try {
                val file = File("$pathName/$fileName")
                file.writeBytes(fileBytes)
                logger.debug { "Fichero guardado en: ${file.absolutePath}" }
                mapOf("path" to file.absolutePath, "name" to file.name)
            } catch (e: Exception) {
                throw FileNotSaveException("Error al guardar el fichero: ${e.message}")
            }
        }

    override fun getFile(pathName: String, fileName: String): File {
        val file = File("$pathName/$fileName")
        if (!file.exists()) {
            throw FileNotFoundException("No se ha encontrado el fichero: $fileName")
        } else {
            return file
        }
    }

    override fun deleteFile(pathName: String, fileName: String): Boolean {
        val file = File("$pathName/$fileName")
        if (!file.exists()) {
            throw FileNotFoundException("No se ha encontrado el fichero: $fileName")
        } else {
            file.delete()
            return true
        }
    }

}