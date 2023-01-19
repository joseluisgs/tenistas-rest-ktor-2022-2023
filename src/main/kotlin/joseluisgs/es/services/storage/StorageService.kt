package joseluisgs.es.services.storage

import io.ktor.utils.io.*
import java.io.File

interface StorageService {
    fun initStorageDirectory()
    suspend fun saveFile(pathName: String, fileName: String, fileBytes: ByteArray): Map<String, String>
    suspend fun saveFile(fileName: String, fileBytes: ByteReadChannel): Map<String, String>
    fun getFile(fileName: String): File
    fun deleteFile(pathName: String, fileName: String): Boolean
}