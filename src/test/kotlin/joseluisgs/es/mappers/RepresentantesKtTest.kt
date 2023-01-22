package joseluisgs.es.mappers

import joseluisgs.es.dto.RepresentanteDto
import joseluisgs.es.models.Representante
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime
import java.util.*


class RepresentantesKtTest {

    val representante = Representante(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        deleted = false
    )

    val representanteDto = RepresentanteDto(
        id = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"),
        nombre = "Test",
        email = "test@example.com",
        metadata = RepresentanteDto.MetaData(
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            deleted = false
        )
    )

    @Test
    fun toDto() {
        val dto = representante.toDto()
        assertAll(
            { assertEquals(representanteDto.id, dto.id) },
            { assertEquals(representanteDto.nombre, dto.nombre) },
            { assertEquals(representanteDto.email, dto.email) }
        )
    }

    @Test
    fun toModel() {
        val model = representanteDto.toModel()
        assertAll(
            { assertEquals(representante.nombre, model.nombre) },
            { assertEquals(representante.email, model.email) },
        )
    }
}