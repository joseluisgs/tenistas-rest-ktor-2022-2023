package joseluisgs.es.db

import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante
import java.util.*

// Datos de prueba
fun getRepresentantesInit() = listOf(
    Representante(
        id = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        email = "pepe@perez.com"
    ),
    Representante(
        id = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3"),
        nombre = "Juan Lopez",
        email = "juan@lopez.com"
    ),
    Representante(
        id = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd"),
        nombre = "Maria Garcia",
        email = "maria@garcia.com"
    ),
)

fun getRaquetasInit() = listOf(
    Raqueta(
        id = UUID.fromString("86084458-4733-4d71-a3db-34b50cd8d68f"),
        marca = "Babolat",
        precio = 200.0,
        represetanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    ),
    Raqueta(
        id = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e"),
        marca = "Wilson",
        precio = 250.0,
        represetanteId = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3")
    ),
    Raqueta(
        id = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2"),
        marca = "Head",
        precio = 225.0,
        represetanteId = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd")
    ),
)