package joseluisgs.es.db

import models.Raqueta
import models.Representante

// Datos de prueba
fun getRepresentantesInit() = listOf(
    Representante(id = 1, nombre = "Pepe Perez", email = "pepe@perez.com"),
    Representante(id = 2, nombre = "Juan Lopez", email = "juan@lopez.com"),
    Representante(id = 3, nombre = "Maria Garcia", email = "maria@garcia.com"),
)

fun getRaquetasInit() = listOf(
    Raqueta(
        id = 1,
        marca = "Babolat",
        precio = 200.0,
        represetanteId = 1
    ),
    Raqueta(
        id = 2,
        marca = "Wilson",
        precio = 250.0,
        represetanteId = 2
    ),
    Raqueta(
        id = 3,
        marca = "Head",
        precio = 225.0,
        represetanteId = 3
    ),
)