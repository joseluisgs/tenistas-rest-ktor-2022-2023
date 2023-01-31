package joseluisgs.es.db

import joseluisgs.es.models.Raqueta
import joseluisgs.es.models.Representante
import joseluisgs.es.models.Tenista
import joseluisgs.es.models.User
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDate
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
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    ),
    Raqueta(
        id = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e"),
        marca = "Wilson",
        precio = 250.0,
        representanteId = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3")
    ),
    Raqueta(
        id = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2"),
        marca = "Head",
        precio = 225.0,
        representanteId = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd")
    ),
)

// Tenistas
fun getTenistasInit() = listOf(
    Tenista(
        id = UUID.fromString("ea2962c6-2142-41b8-8dfb-0ecfe67e27df"),
        nombre = "Rafael Nadal",
        ranking = 2,
        fechaNacimiento = LocalDate.parse("1985-06-04"),
        añoProfesional = 2005,
        altura = 185,
        peso = 81,
        manoDominante = Tenista.ManoDominante.IZQUIERDA,
        tipoReves = Tenista.TipoReves.DOS_MANOS,
        puntos = 6789,
        pais = "España",
        raquetaId = UUID.fromString("86084458-4733-4d71-a3db-34b50cd8d68f")
    ),
    Tenista(
        id = UUID.fromString("f629e649-c6b7-4514-94a8-36bbcd4e7e1b"),
        nombre = "Roger Federer",
        ranking = 3,
        fechaNacimiento = LocalDate.parse("1981-01-01"),
        añoProfesional = 2000,
        altura = 188,
        peso = 83,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.UNA_MANO,
        puntos = 3789,
        pais = "Suiza",
        raquetaId = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e")
    ),
    Tenista(
        id = UUID.fromString("24242ae7-1c81-434f-9b33-849a640d68a0"),
        nombre = "Novak Djokovic",
        ranking = 4,
        fechaNacimiento = LocalDate.parse("1986-05-05"),
        añoProfesional = 2004,
        altura = 189,
        peso = 81,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.DOS_MANOS,
        puntos = 1970,
        pais = "Serbia",
        raquetaId = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2")
    ),
    Tenista(
        id = UUID.fromString("af04e495-bacc-4bde-8d61-d52f78b52a86"),
        nombre = "Dominic Thiem",
        ranking = 5,
        fechaNacimiento = LocalDate.parse("1985-06-04"),
        añoProfesional = 2015,
        altura = 188,
        peso = 82,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.UNA_MANO,
        puntos = 1234,
        pais = "Austria",
        raquetaId = UUID.fromString("86084458-4733-4d71-a3db-34b50cd8d68f")
    ),
    Tenista(
        id = UUID.fromString("a711040a-fb0d-4fe4-b726-75883ca8d907"),
        nombre = "Carlos Alcaraz",
        ranking = 1,
        fechaNacimiento = LocalDate.parse("2003-05-05"),
        añoProfesional = 2019,
        altura = 185,
        peso = 80,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.DOS_MANOS,
        puntos = 6880,
        pais = "España",
        raquetaId = UUID.fromString("86084458-4733-4d71-a3db-34b50cd8d68f")
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


