package joseluisgs.es.db

import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante
import joseluisgs.es.models.User
import org.mindrot.jbcrypt.BCrypt
import java.util.*

// Datos de prueba

// Representantes
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

// Raquetas
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

// Usuarios
fun getUsuariosInit() = listOf(
    User(
        id = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        username = "pepe",
        email = "pepe@perez.com",
        password = BCrypt.hashpw("pepe1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.ADMIN
    ),
    User(
        id = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3"),
        nombre = "Ana Lopez",
        username = "ana",
        email = "ana@lopez.com",
        password = BCrypt.hashpw("ana1234", BCrypt.gensalt(12)),
        avatar = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        role = User.Role.USER
    )
)

