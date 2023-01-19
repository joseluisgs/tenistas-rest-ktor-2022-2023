package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserDto
import joseluisgs.es.dto.UserLoginDto
import joseluisgs.es.dto.UserWithTokenDto
import joseluisgs.es.exceptions.UserBadRequestException
import joseluisgs.es.exceptions.UserNotFoundException
import joseluisgs.es.exceptions.UserUnauthorizedException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.models.User
import joseluisgs.es.services.storage.StorageService
import joseluisgs.es.services.tokens.TokensService
import joseluisgs.es.services.users.UsersService
import joseluisgs.es.utils.toUUID
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/users" // Ruta de acceso, puede aunar un recurso

fun Application.usersRoutes() {

    val usersService: UsersService by inject()
    val tokenService: TokensService by inject()
    val storageService: StorageService by inject()

    routing {
        route("/$ENDPOINT") {

            // Post -> /register
            post("/register") {
                logger.debug { "POST Register /$ENDPOINT/register" }
                try {
                    val dto = call.receive<UserCreateDto>()
                    val user = usersService.save(dto.toModel())
                    call.respond(HttpStatusCode.Created, user.toDto())
                } catch (e: UserBadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                }
            }

            // Post -> /login
            post("/login") {
                logger.debug { "POST Login /$ENDPOINT/login" }
                try {
                    val dto = call.receive<UserLoginDto>()
                    val user = usersService.checkUserNameAndPassword(dto.username, dto.password)
                    user?.let {
                        val token = tokenService.generateJWTToken(user)
                        call.respond(HttpStatusCode.OK, UserWithTokenDto(user.toDto(), token))
                    }
                } catch (e: UserUnauthorizedException) {
                    call.respond(HttpStatusCode.Unauthorized, e.message.toString())
                } catch (e: UserBadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                } catch (e: RequestValidationException) {
                    call.respond(HttpStatusCode.BadRequest, e.reasons)
                }
            }

            // Estas rutas están autenticadas --> Protegidas por JWT
            authenticate {
                // Get -> /me
                get("/me") {
                    logger.debug { "GET Me /$ENDPOINT/me" }
                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    try {
                        val jwt = call.principal<JWTPrincipal>()
                        // Cuidado que vienen con comillas!!!
                        // val username = jwt?.payload?.getClaim("username").toString().replace("\"", "")
                        val userId = jwt?.payload?.getClaim("userId")
                            .toString().replace("\"", "")
                        val user = usersService.findById(userId.toUUID())
                        user.let {
                            call.respond(HttpStatusCode.OK, user.toDto())
                        }
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado o no autenticado")
                    }
                }

                put("/me/avatar") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }
                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    try {
                        val jwt = call.principal<JWTPrincipal>()
                        // Cuidado que vienen con comillas!!!
                        // val username = jwt?.payload?.getClaim("username").toString().replace("\"", "")
                        val userId = jwt?.payload?.getClaim("userId")
                            .toString().replace("\"", "")
                        val user = usersService.findById(userId.toUUID())
                        // ya tenemos el usuario, ahora actualizamos porque es multiparte
                        // Pueden venir los datos y la imagen
                        logger.debug { "Tomando datos multiparte" }
                        // Lo recorremos
                        val multipartData = call.receiveMultipart()
                        var fileDescription = ""
                        multipartData.forEachPart { part ->
                            // Analizamos el tipo si es fichero
                            when (part) {
                                is PartData.FormItem -> {
                                    fileDescription = part.value
                                    logger.debug { "El tipo de formulario: $fileDescription" }
                                }

                                is PartData.FileItem -> {
                                    val fileName = part.originalFileName as String
                                    val fileBytes = part.streamProvider().readBytes()
                                    val fileExtension = fileName.substringAfterLast(".")
                                    val newFileName = "$userId.$fileExtension"
                                    val res = storageService.saveFile(newFileName, fileBytes)
                                    println(res)
                                }

                                else -> {
                                    logger.debug { "No es un fichero" }
                                }
                            }
                            part.dispose()
                        }

                        user.let {
                            call.respond(HttpStatusCode.OK, user.toDto())
                        }
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado o no autenticado")
                    }
                }


                // Get -> /users --> solo si eres admin
                get("/list") {
                    logger.debug { "GET Users /$ENDPOINT/list" }
                    try {
                        val jwt = call.principal<JWTPrincipal>()
                        // Cuidado que vienen con comillas!!!
                        // val username = jwt?.payload?.getClaim("username").toString().replace("\"", "")
                        val userId = jwt?.payload?.getClaim("userId")
                            .toString().replace("\"", "")
                        // Buscamos el usuario
                        val user = usersService.findById(userId.toUUID())
                        user.let {
                            // Si es admin
                            if (user.role == User.Role.ADMIN) {
                                // Devolvemos todos los usuarios
                                val res = mutableListOf<UserDto>()
                                usersService.findAll(null).collect {
                                    res.add(it.toDto())
                                }
                                call.respond(HttpStatusCode.OK, res)
                            } else {
                                // Si no es admin, no puede ver la lista de usuarios
                                call.respond(
                                    HttpStatusCode.Unauthorized,
                                    "No estas autorizado a realizar esta operación"
                                )
                            }
                        }
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado o no autenticado")
                    }
                }
            }
        }
    }
}