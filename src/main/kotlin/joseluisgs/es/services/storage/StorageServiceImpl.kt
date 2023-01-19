package joseluisgs.es.services.storage

import io.ktor.util.cio.*
import io.ktor.utils.io.*
import joseluisgs.es.config.StorageConfig
import joseluisgs.es.exceptions.StorageFileNotFoundException
import joseluisgs.es.exceptions.StorageFileNotSaveException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.io.File
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Single
class StorageServiceImpl(
    private val storageConfig: StorageConfig
) : StorageService {

    init {
        logger.debug { "Iniciando servicio de almacenamiento en: ${storageConfig.uploadDir}" }
    }

    override fun getConfig(): StorageConfig {
        return storageConfig
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

    override suspend fun saveFile(fileName: String, fileBytes: ByteArray): Map<String, String> =
        withContext(Dispatchers.IO) {
            try {
                val file = File("${storageConfig.uploadDir}/$fileName")
                file.writeBytes(fileBytes) // sobreescritura si existe
                logger.debug { "Fichero guardado en: ${file.absolutePath}" }
                return@withContext mapOf(
                    "fileName" to fileName,
                    "createdAt" to LocalDateTime.now().toString(),
                    "size" to fileBytes.size.toString(),
                    "baseUrl" to storageConfig.baseUrl + "/" + storageConfig.endpoint + "/" + fileName,
                    "secureUrl" to storageConfig.secureUrl + "/" + storageConfig.endpoint + "/" + fileName,
                )
            } catch (e: Exception) {
                throw StorageFileNotSaveException("Error al guardar el fichero: ${e.message}")
            }
        }

    override suspend fun saveFile(fileName: String, fileBytes: ByteReadChannel): Map<String, String> =
        withContext(Dispatchers.IO) {
            try {
                logger.debug { "Guardando fichero en: $fileName" }
                val file = File("${storageConfig.uploadDir}/$fileName")
                val res = fileBytes.copyAndClose(file.writeChannel())
                logger.debug { "Fichero guardado en: $file" }
                return@withContext mapOf(
                    "fileName" to fileName,
                    "createdAt" to LocalDateTime.now().toString(),
                    "size" to res.toString(),
                    "baseUrl" to storageConfig.baseUrl + "/" + storageConfig.endpoint + "/" + fileName,
                    "secureUrl" to storageConfig.secureUrl + "/" + storageConfig.endpoint + "/" + fileName,
                )
            } catch (e: Exception) {
                throw StorageFileNotSaveException("Error al guardar el fichero: ${e.message}")
            }
        }

    override suspend fun getFile(fileName: String): File = withContext(Dispatchers.IO) {
        logger.debug { "Buscando fichero en: $fileName" }
        val file = File("${storageConfig.uploadDir}/$fileName")
        logger.debug { "Fichero path: $file" }
        if (!file.exists()) {
            throw StorageFileNotFoundException("No se ha encontrado el fichero: $fileName")
        } else {
            return@withContext file
        }
    }

    override suspend fun deleteFile(fileName: String): Unit = withContext(Dispatchers.IO) {
        logger.debug { "Borrando fichero en: $fileName" }
        val file = File("${storageConfig.uploadDir}/$fileName")
        logger.debug { "Fichero path: $file" }
        if (!file.exists()) {
            throw StorageFileNotFoundException("No se ha encontrado el fichero: $fileName")
        } else {
            file.delete()
        }
    }

}