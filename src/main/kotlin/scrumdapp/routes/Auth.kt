package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.middleware.IsLoggedOut
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordGuild
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.pages.loginPage
import com.jeroenvdg.scrumdapp.views.mainLayout
import io.github.cdimascio.dotenv.Dotenv
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
import scrumdapp.services.EnvironmentService

@Serializable
data class UserSession(val tokenData: TokenData, val userData: UserData)

@Serializable
data class TokenData(val accessToken: String, val tokenType: String, val refreshToken: String, val accessTokenExpiresAt: GMTDate)

@Serializable
data class UserData(val name: String, val discordId: String, val avatar: String?)

suspend fun Application.authRouting() {
    val env = dependencies.resolve<EnvironmentService>()
    val httpClient = dependencies.resolve<HttpClient>()
    val discordService = dependencies.resolve<DiscordService>()

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
                    val currentPrincipal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    val state = currentPrincipal?.state
                    if (currentPrincipal == null) return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                    if (state == null) return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")

                    val tokenExpirationDate = GMTDate() + currentPrincipal.expiresIn * 1000
                    val tokenData = TokenData(currentPrincipal.accessToken, currentPrincipal.tokenType, currentPrincipal.refreshToken!!, tokenExpirationDate)

                    val guildsResponse: Result<List<DiscordGuild>> = discordService.getGuilds(currentPrincipal.accessToken)
                    if (guildsResponse.isFailure) {
                        return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                    }

                    val guilds = guildsResponse.getOrThrow()
                    var guildFound = false
                    for (guild in guilds.reversed()) {
                        if (guild.id == authorizationServerId) {
                            guildFound = true
                            break
                        }
                    }

                    if (!guildFound) {
                        return@get call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                    }

                    val user = discordService.getUser(currentPrincipal.accessToken).getOrThrow()
                    val guildMember = discordService.getGuildMember(currentPrincipal.accessToken, authorizationServerId).getOrThrow()
                    val name = guildMember.nick ?: user.global_name
                    val avatar = guildMember.avatar ?: user.avatar

                    call.sessions.set(UserSession(tokenData, UserData(name, user.id, avatar)))
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
    }
}


suspend fun ApplicationCall.tryGetUserSession(): UserSession? {
    val userSession = this.sessions.get<UserSession>()
    if (userSession == null) {
        this.respondRedirect("/", false)
        return null
    }
    return userSession
}