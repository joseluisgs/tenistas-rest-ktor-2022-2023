package joseluisgs.es.services.storage

import com.github.michaelbull.result.Result
import io.ktor.utils.io.*
import joseluisgs.es.config.StorageConfig
import joseluisgs.es.errors.StorageError
import java.io.File

interface StorageService {
    fun getConfig(): StorageConfig
    fun initStorageDirectory()
    suspend fun getFile(fileName: String): Result<File, StorageError>
    suspend fun deleteFile(fileName: String): Result<String, StorageError>
    suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): Result<Map<String, String>, StorageError>

    suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteReadChannel
    ): Result<Map<String, String>, StorageError>
}