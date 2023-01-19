package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.*
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
            // datos del usuario
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

                // Actualizar datos del usuario
                put("/me") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }
                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    try {
                        val jwt = call.principal<JWTPrincipal>()
                        // Cuidado que vienen con comillas!!!
                        // val username = jwt?.payload?.getClaim("username").toString().replace("\"", "")
                        val userId = jwt?.payload?.getClaim("userId")
                            .toString().replace("\"", "").toUUID()
                        val user = usersService.findById(userId)
                        // Tomamos l
                        val dto = call.receive<UserUpdateDto>()
                        user.let {
                            var userUpdated = user.copy(
                                nombre = dto.nombre,
                                username = dto.username,
                                email = dto.email,
                            )
                            userUpdated = usersService.update(userId, userUpdated)!!
                            call.respond(HttpStatusCode.OK, userUpdated.toDto())
                        }
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado o no autenticado")
                    } catch (e: RequestValidationException) {
                        call.respond(HttpStatusCode.BadRequest, e.reasons)
                    } catch (e: UserBadRequestException) {
                        call.respond(HttpStatusCode.BadRequest, e.message.toString())
                    }
                }

                // Actualizar avatar del usuario
                // Otra forma de subir imaagenes con multipart
                patch("/me") {
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
                        var newFileName: String = ""
                        val multipartData = call.receiveMultipart()
                        multipartData.forEachPart { part ->
                            // Analizamos el tipo si es fichero
                            if (part is PartData.FileItem) {
                                val fileName = part.originalFileName as String
                                val fileBytes = part.streamProvider().readBytes()
                                val fileExtension = fileName.substringAfterLast(".")
                                newFileName = "$userId.$fileExtension"
                                val res = storageService.saveFile(newFileName, fileBytes)
                                // Dependiendo de si estamos en SSL siempre o sin SSL salvamos la ruta
                                newFileName = if (call.request.origin.scheme == "https") {
                                    //"https://${call.request.host()}:6963/api/storage/$newFileName"
                                    res["secureUrl"].toString()
                                } else {
                                    //"http://${call.request.host()}:6969/api/storage/$newFileName"
                                    res["baseUrl"].toString()

                                }
                            }
                            part.dispose()
                        }

                        user.let {
                            val userUpdated = user.copy(avatar = newFileName)
                            usersService.update(user.id, userUpdated)
                            call.respond(HttpStatusCode.OK, userUpdated.toDto())
                        }
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.Unauthorized, "Usuario no encontrado o no autenticado")
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, e.message.toString())
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