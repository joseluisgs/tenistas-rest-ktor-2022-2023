package joseluisgs.es.utils


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class UuidUtilsKtTest {

    @Test
    fun toUUID() {
        val uuid = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
        assertEquals(uuid, uuid.toUUID().toString())
    }

    @Test
    fun toUUID2Exception() {
        val uuid = "a0eebc99"
        val exception = assertFailsWith<UUIDException> {
            uuid.toUUID()
        }
        assertEquals("El id no es válido o no está en el formato UUID", exception.message)
    }
}