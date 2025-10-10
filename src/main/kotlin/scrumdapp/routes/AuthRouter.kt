package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.SessionRepository
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsLoggedOut
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.services.EnvironmentService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordUser
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.pages.loginPage
import com.jeroenvdg.scrumdapp.views.mainLayout
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.plugins.di.*
import io.ktor.server.resources.href
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable

@Serializable
data class SessionToken(val token: String)

@Resource("/auth")
class AuthRouter() {
    @Resource("login")
    class Login(val parent: AuthRouter = AuthRouter())
    @Resource("callback")
    class Callback(val parent: AuthRouter = AuthRouter())
}

@Resource("/login")
class LoginRouter()

@Resource("/logout")
class LogoutRouter()

suspend fun Application.configureAuthRouting() {
    val env = dependencies.resolve<EnvironmentService>()
    val httpClient = dependencies.resolve<HttpClient>()
    val discordService = dependencies.resolve<DiscordService>()
    val userRepository = dependencies.resolve<UserRepository>()
    val sessionRepository = dependencies.resolve<SessionRepository>()

    val authorizationServerId = env.getVariable("AUTHORIZATION_SERVER_ID")

    install(Authentication) {
        oauth("auth-oauth-discord") {
            urlProvider = { env.getVariable("DISCORD_OAUTH_CALLBACK") }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "discord",
                    authorizeUrl = "https://discord.com/oauth2/authorize",
                    accessTokenUrl = "https://discord.com/api/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = env.getVariable("DISCORD_OAUTH_ID"),
                    clientSecret = env.getVariable("DISCORD_OAUTH_SECRET"),
                    defaultScopes = listOf("identify", "guilds", "guilds.members.read"),
                )
            }
            client = httpClient
        }
    }

    routing {
        route<AuthRouter> {
            install(IsLoggedOut)
            authenticate("auth-oauth-discord") {
                route<AuthRouter.Login> {
                    get {  } // Gets overridden bij authenticate fn
                }
                route<AuthRouter.Callback> {
                    get {
                        val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                        val state = principal?.state
                        if (principal == null) return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                        if (principal.refreshToken == null) return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                        if (state == null) return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")

                        val tokenExpiry = GMTDate() + principal.expiresIn * 1000

                        val discordUser = discordService.getUser(principal.accessToken).getOrElse {
                            return@get call.respond(HttpStatusCode.Unauthorized, "Failed to log in")
                        }

                        // Get user with discordId
                        val user = userRepository.getUserFromDiscordId(discordUser.id) ?: createUser(principal, discordUser, authorizationServerId, discordService, userRepository)
                        val session = sessionRepository.createSession(user.id, principal.refreshToken!!, principal.accessToken, tokenExpiry)

                        call.sessions.set(SessionToken(session.token))
                        val redirect = call.sessions.get<RedirectCookie>()
                        if (redirect != null) {
                            call.sessions.clear<RedirectCookie>()
                            call.respondRedirect(redirect.to)
                        } else {
                            call.respondRedirect("/home")
                        }
                    }
                }
            }
        }

        route<LoginRouter> {
            install(IsLoggedOut)
            get {
                call.respondHtml {
                    mainLayout(PageData("Login")) {
                        loginPage(application)
                    }
                }
            }
        }

        route<LogoutRouter> {
            install(IsLoggedIn)
            get {
                val token = call.userSession.token
                sessionRepository.deleteSession(token)
                call.sessions.clear<SessionToken>()
                call.respondRedirect(href(LoginRouter()))
            }
        }
    }
}

suspend fun createUser(principal: OAuthAccessTokenResponse.OAuth2,
                       discordUser: DiscordUser,
                       authorityGuild: String,
                       discordService: DiscordService,
                       userRepository: UserRepository): User {

    val guildMember = discordService.getGuildMember(principal.accessToken, authorityGuild).getOrThrow()
    val name = guildMember.nick ?: discordUser.global_name
    var avatar = guildMember.avatar ?: discordUser.avatar
    if (avatar != null) { avatar = "https://cdn.discordapp.com/avatars/${discordUser.id}/${avatar}.png" }

    val user = userRepository.addUser(User(-1, name, discordUser.id.toLong(), avatar ?: ""))
    return user!!
}
