package joseluisgs.es.services.storage

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import joseluisgs.es.config.StorageConfig
import joseluisgs.es.errors.StorageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Single
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
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
        Files.createDirectories(Path.of(storageConfig.uploadDir))
        if (storageConfig.environment == "dev") {
            logger.debug { "Modo de desarrollo. Borrando el contenido del almacenamiento" }
            File(storageConfig.uploadDir).listFiles()?.forEach { it.delete() }
        }
    }

    // Esta es la forma que más me gusta usando multipart

    override suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): Result<Map<String, String>, StorageError> =
        withContext(Dispatchers.IO) {
            logger.debug { "Saving file in: $fileName" }
            return@withContext try {
                File("${storageConfig.uploadDir}/$fileName").writeBytes(fileBytes)
                Ok(
                    mapOf(
                        "fileName" to fileName,
                        "createdAt" to LocalDateTime.now().toString(),
                        "size" to fileBytes.size.toString(),
                        "url" to fileUrl,
                    )
                )
            } catch (e: Exception) {
                Err(StorageError.FileNotSave("Error saving file: $fileName"))
            }
        }

    // Otra forma que me gusta menos usando canales, muy efectiva
    // pero pirdes alguna información del fichero
    override suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteReadChannel
    ): Result<Map<String, String>, StorageError> =
        withContext(Dispatchers.IO) {
            logger.debug { "Saving file in: $fileName" }
            return@withContext try {
                val res = fileBytes.copyAndClose(File("${storageConfig.uploadDir}/$fileName").writeChannel())
                Ok(
                    mapOf(
                        "fileName" to fileName,
                        "createdAt" to LocalDateTime.now().toString(),
                        "size" to res.toString(),
                        "url" to fileUrl,
                    )
                )
            } catch (e: Exception) {
                Err(StorageError.FileNotSave("Error saving file: $fileName"))
            }
        }

    override suspend fun getFile(fileName: String): Result<File, StorageError> = withContext(Dispatchers.IO) {
        logger.debug { "Get file: $fileName" }
        return@withContext if (!File("${storageConfig.uploadDir}/$fileName").exists()) {
            Err(StorageError.FileNotFound("File Not Found in storage: $fileName"))
        } else {
            Ok(File("${storageConfig.uploadDir}/$fileName"))
        }
    }

    override suspend fun deleteFile(fileName: String): Result<String, StorageError> = withContext(Dispatchers.IO) {
        logger.debug { "Remove file: $fileName" }
        Files.deleteIfExists(Path.of("${storageConfig.uploadDir}/$fileName"))
        Ok(fileName)
    }

}