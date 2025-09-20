package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
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
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.serialization.Serializable
import org.h2.engine.User
import java.util.Optional

@Serializable
data class UserSession(val state: String, val token: String)

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
                    defaultScopes = listOf("identify", "guilds", "email"),
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

                    call.sessions.set(UserSession(state, currentPrincipal.accessToken))
                    call.respondRedirect("/home")
                }
            }
        }

        get("/home") {
            val user = call.tryGetUserSession() ?: return@get
            val discordUser = discordService.getUser(user.token)
            call.respondText("Hello, ${discordUser.global_name}")
        }

        get("/guilds") {
            val user = call.tryGetUserSession() ?: return@get
            val guilds = discordService.getGuilds(user.token)
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