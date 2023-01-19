package joseluisgs.es.services.storage

import java.io.File

interface StorageService {
    fun initStorageDirectory()
    suspend fun saveFile(pathName: String, fileName: String, fileBytes: ByteArray): Map<String, String>
    fun getFile(pathName: String, fileName: String): File
    fun deleteFile(pathName: String, fileName: String): Boolean
}