package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.SessionService
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsLoggedOut
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.services.EnvironmentService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordUser
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.pages.loginPage
import com.jeroenvdg.scrumdapp.views.mainLayout
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.serialization.Serializable

@Serializable
data class SessionToken(val token: String)

suspend fun Application.configureAuthRouting() {
    val env = dependencies.resolve<EnvironmentService>()
    val httpClient = dependencies.resolve<HttpClient>()
    val discordService = dependencies.resolve<DiscordService>()
    val userService = dependencies.resolve<UserService>()
    val sessionService = dependencies.resolve<SessionService>()

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
        route("/auth") {
            install(IsLoggedOut)
            authenticate("auth-oauth-discord") {
                get("/login") { } // Magically redirects to the callback
                get("/callback") {
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
                    val user = userService.getUser(1) ?: createUser(principal, discordUser, authorizationServerId, discordService, userService)
                    val session = sessionService.createSession(user.id, principal.refreshToken!!, principal.accessToken, tokenExpiry)

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

        route("/login") {
            install(IsLoggedOut)
            get {
                call.respondHtml {
                    mainLayout(PageData("Login")) {
                        loginPage()
                    }
                }
            }
        }

        route("/logout") {
            install(IsLoggedIn)
            get {
                val token = call.userSession.token
                sessionService.deleteSession(token)
                call.sessions.clear<SessionToken>()
                call.respondRedirect("/login")
            }
        }
    }
}

suspend fun createUser(principal: OAuthAccessTokenResponse.OAuth2,
                       discordUser: DiscordUser,
                       authorityGuild: String,
                       discordService: DiscordService,
                       userService: UserService): User {

    val guildMember = discordService.getGuildMember(principal.accessToken, authorityGuild).getOrThrow()
    val name = guildMember.nick ?: discordUser.global_name
    var avatar = guildMember.avatar ?: discordUser.avatar
    if (avatar != null) { avatar = "https://cdn.discordapp.com/avatars/${discordUser.id}/${avatar}.png" }

    val user = userService.addUser(User(-1, name, discordUser.id.toLong(), avatar ?: ""))
    return user!!
}
