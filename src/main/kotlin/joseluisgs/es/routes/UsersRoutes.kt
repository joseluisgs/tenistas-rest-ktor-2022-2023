package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserLoginDto
import joseluisgs.es.dto.UserUpdateDto
import joseluisgs.es.dto.UserWithTokenDto
import joseluisgs.es.errors.StorageError
import joseluisgs.es.errors.UserError
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.storage.StorageService
import joseluisgs.es.services.tokens.TokensService
import joseluisgs.es.services.users.UsersService
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.toList
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

                val dto = call.receive<UserCreateDto>().toModel()
                usersService.save(dto)
                    .mapBoth(
                        success = { call.respond(HttpStatusCode.Created, it.toDto()) },
                        failure = { handleUserError(it) }
                    )
            }

            // Post -> /login
            post("/login") {
                logger.debug { "POST Login /$ENDPOINT/login" }

                val dto = call.receive<UserLoginDto>()
                usersService.checkUserNameAndPassword(dto.username, dto.password)
                    .mapBoth(
                        success = { user ->
                            val token = tokenService.generateJWT(user)
                            call.respond(HttpStatusCode.OK, UserWithTokenDto(user.toDto(), token))
                        },
                        failure = { handleUserError(it) }
                    )
            }

            // Estas rutas están autenticadas --> Protegidas por JWT
            // datos del usuario
            authenticate {
                // Get -> /me
                get("/me") {
                    logger.debug { "GET Me /$ENDPOINT/me" }

                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    // Cuidado que viene con comillas!!!
                    val userUuid = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toUUID()

                    usersService.findById(userUuid)
                        .mapBoth(
                            success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                            failure = { handleUserError(it) }
                        )
                }

                // Actualizar datos del usuario
                put("/me") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }

                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    // Cuidado que vienen con comillas!!!
                    val userUuid = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toUUID()

                    val dto = call.receive<UserUpdateDto>()

                    usersService.findById(userUuid).andThen {
                        usersService.update(
                            userUuid, it.copy(
                                nombre = dto.nombre,
                                username = dto.username,
                                email = dto.email,
                            )
                        )
                    }.mapBoth(
                        success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                        failure = { handleUserError(it) }
                    )
                }

                // Actualizar avatar del usuario
                // Otra forma de subir imaagenes con multipart
                patch("/me") {
                    logger.debug { "PUT Me /$ENDPOINT/me" }

                    // Por el token me llega como principal (autenticado) el usuario en sus claims
                    val userUuid = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toUUID()

                    val baseUrl =
                        call.request.origin.scheme + "://" + call.request.host() + ":" + call.request.port() + "/api/storage/"
                    logger.debug { "Tomando datos multiparte" }

                    val multipartData = call.receiveMultipart()
                    multipartData.forEachPart { part ->
                        // Analizamos el tipo si es fichero
                        if (part is PartData.FileItem) {
                            val fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()
                            val fileExtension = fileName.substringAfterLast(".")
                            val newFileName = "$userUuid.$fileExtension"
                            val newFileUrl = "$baseUrl$newFileName"
                            // Buscar usuario,
                            usersService.findById(userUuid).onSuccess {
                                // Subir fichero
                                storageService.saveFile(newFileName, newFileUrl, fileBytes)
                            }.andThen {
                                // Actualizar usuario
                                usersService.update(
                                    userUuid, it.copy(
                                        avatar = newFileUrl
                                    )
                                )
                            }.mapBoth(
                                success = { call.respond(HttpStatusCode.OK, it.toDto()) },
                                failure = { handleUserError(it) }
                            )
                        }
                    }
                }

                // Get -> /users --> solo si eres admin
                get("/list") {
                    logger.debug { "GET Users /$ENDPOINT/list" }

                    val userUuid = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toUUID()

                    usersService.isAdmin(userUuid)
                        .onSuccess {
                            usersService.findAll(null).toList()
                                .map { it.toDto() }
                                .let { call.respond(HttpStatusCode.OK, it) }
                        }.onFailure {
                            handleUserError(it)
                        }
                }

                // Eliminar usuario --> solo si eres admin
                delete("delete/{id}") {
                    logger.debug { "DELETE User /$ENDPOINT/{id}" }

                    val userUuid = call.principal<JWTPrincipal>()
                        ?.payload?.getClaim("userId")
                        .toString().replace("\"", "").toUUID()

                    val id = call.parameters["id"]?.toUUID()!!

                    usersService.isAdmin(userUuid).andThen {
                        // buscar usuario
                        usersService.findById(id).andThen { user ->
                            // eliminar usuario
                            usersService.delete(user.id).andThen {
                                // elimina el avatar
                                storageService.deleteFile(user.avatar)
                            }
                        }
                    }.mapBoth(
                        success = { call.respond(HttpStatusCode.NoContent) },
                        failure = { handleUserError(it) }
                    )

                }
            }
        }
    }
}

// Manejador de errores
private suspend fun PipelineContext<Unit, ApplicationCall>.handleUserError(
    error: Any
) {
    when (error) {
        // Errores de usuario
        is UserError.BadRequest -> call.respond(HttpStatusCode.BadRequest, error.message)
        is UserError.NotFound -> call.respond(HttpStatusCode.NotFound, error.message)
        is UserError.Unauthorized -> call.respond(HttpStatusCode.Unauthorized, error.message)
        is UserError.Forbidden -> call.respond(HttpStatusCode.Forbidden, error.message)
        // Storage
        is StorageError.FileNotFound -> call.respond(HttpStatusCode.NotFound, error.message)
    }
}