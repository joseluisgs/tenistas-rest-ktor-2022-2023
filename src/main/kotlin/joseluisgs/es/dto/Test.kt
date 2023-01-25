package joseluisgs.es.dto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TestDto(
    val message: String,
    val createdAt: String = LocalDateTime.now().toString()
)