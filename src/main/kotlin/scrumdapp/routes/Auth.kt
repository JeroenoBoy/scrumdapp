package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordGuild
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordUser
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.OAuthAuthenticationProvider
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.oauth
import io.ktor.server.auth.principal
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.header
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.date.GMTDate
import io.ktor.util.date.plus
import jdk.internal.org.jline.keymap.KeyMap.alt
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.strong
import kotlinx.html.title
import kotlinx.serialization.Serializable
import org.h2.engine.User
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Optional
import kotlin.time.Duration

@Serializable
data class UserSession(val tokenData: TokenData, val userData: UserData)

@Serializable
data class TokenData(val accessToken: String, val tokenType: String, val refreshToken: String, val accessTokenExpiresAt: GMTDate)

@Serializable
data class UserData(val name: String, val discordId: String, val avatar: String?)

suspend fun Application.authRouting() {
    val dotenv = dependencies.resolve<Dotenv>()
    val httpClient = dependencies.resolve<HttpClient>()
    val discordService = dependencies.resolve<DiscordService>()

    val authorizationServerId = System.getenv("AUTHORIZATION_SERVER_ID") ?: dotenv.get("AUTHORIZATION_SERVER_ID") ?: throw Exception("AUTHORIZATION_SERVER_ID is not defined")

    install(Authentication) {
        oauth("auth-oauth-discord") {
            urlProvider = { System.getenv("DISCORD_OAUTH_CALLBACK") ?: dotenv["DISCORD_OAUTH_CALLBACK"] }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "discord",
                    authorizeUrl = "https://discord.com/oauth2/authorize",
                    accessTokenUrl = "https://discord.com/api/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("DISCORD_OAUTH_ID") ?: dotenv.get("DISCORD_OAUTH_ID"),
                    clientSecret = System.getenv("DISCORD_OAUTH_SECRET") ?: dotenv.get("DISCORD_OAUTH_SECRET"),
                    defaultScopes = listOf("identify", "guilds", "guilds.members.read"),
                )
            }
            client = httpClient
        }
    }

    routing {
        route("/auth") {
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
                    call.respondRedirect("/home")
                }
            }
        }

        get("/home") {
            val session = call.tryGetUserSession() ?: return@get
            call.respondHtml {
                head {
                    title("Scrum daddy app")
                }
                body {
                    h1 {
                        +"Scrumdapp"
                    }
                    p {
                        +"Hello "
                        strong { +session.userData.name }
                    }
                    if (session.userData.avatar != null) {
                        img(alt="User profile picture", "https://cdn.discordapp.com/avatars/${session.userData.discordId}/${session.userData.avatar}.png")
                    }
                }
            }
        }

        get("/guilds") {
            val user = call.tryGetUserSession() ?: return@get
            val guilds = discordService.getGuilds(user.tokenData.accessToken).getOrThrow()
            call.respondText("Hello, ${guilds.joinToString(", ") { it.name }}")
        }

        get("/") {
            call.respondHtml {
                body {
                    p {
                        a("/auth/login") { +"Login with Discord" }
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