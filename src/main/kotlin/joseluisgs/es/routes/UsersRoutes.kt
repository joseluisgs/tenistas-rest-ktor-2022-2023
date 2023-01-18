package joseluisgs.es.routes

// import org.koin.ktor.ext.get as koinGet // define un alias o te dará problemas con el get de Ktor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import joseluisgs.es.dto.UserCreateDto
import joseluisgs.es.dto.UserLoginDto
import joseluisgs.es.dto.UserWithTokenDto
import joseluisgs.es.exceptions.UserBadRequestException
import joseluisgs.es.exceptions.UserUnauthorizedException
import joseluisgs.es.mappers.toDto
import joseluisgs.es.mappers.toModel
import joseluisgs.es.services.tokens.TokensService
import joseluisgs.es.services.users.UsersService
import mu.KotlinLogging
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

private const val ENDPOINT = "api/users" // Ruta de acceso, puede aunar un recurso

fun Application.usersRoutes() {

    val usersService: UsersService by inject()
    val tokenService: TokensService by inject()

    routing {
        route("/$ENDPOINT") {

            // Post -> /register
            post("/register") {
                logger.debug { "POST Register /$ENDPOINT" }
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
                logger.debug { "POST Login /$ENDPOINT" }
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
        }
    }
}