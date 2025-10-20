package com.jeroenvdg.scrumdapp

import com.jeroenvdg.scrumdapp.Database.initializeDatabase
import com.jeroenvdg.scrumdapp.db.*
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.middleware.UserProvider
import com.jeroenvdg.scrumdapp.routes.SessionToken
import com.jeroenvdg.scrumdapp.routes.configureAuthRouting
import com.jeroenvdg.scrumdapp.routes.configureRouting
import com.jeroenvdg.scrumdapp.routes.groups.configureGroupRoutes
import com.jeroenvdg.scrumdapp.routes.invites.configureInviteRoutes
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.services.DotenvService
import com.jeroenvdg.scrumdapp.services.EncryptionServiceImpl
import com.jeroenvdg.scrumdapp.services.EnvironmentService
import com.jeroenvdg.scrumdapp.services.InviteService
import com.jeroenvdg.scrumdapp.services.UserService
import com.jeroenvdg.scrumdapp.services.configureExceptionService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordServiceImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.di.*
import io.ktor.server.resources.Resources
import io.ktor.server.sessions.*

fun main(args: Array<String>) {
    println("Starting Scrumdapp")
    EngineMain.main(args)
}

suspend fun Application.module() {
    val env = DotenvService()
    val httpClient = HttpClient(CIO) { }
    dependencies { provide<EnvironmentService> { env } }
    val database = initializeDatabase()
    val encryptionService = EncryptionServiceImpl(env)
    val userRepository = UserRepositoryImpl()
    val sessionRepository = SessionRepositoryImpl()
    val groupRepository = GroupRepositoryImpl()
    val checkinRepository = CheckinRepositoryImpl()

    dependencies {
        provide { database }
        provide { httpClient }
        provide { encryptionService }
        provide { UserService(groupRepository) }
        provide { CheckinService(checkinRepository, groupRepository) }
        provide { InviteService(groupRepository, encryptionService) }
        provide<UserRepository> { userRepository }
        provide<GroupRepository> { groupRepository }
        provide<CheckinRepository> { checkinRepository }
        provide<SessionRepository> { sessionRepository }
        provide<DiscordService> { DiscordServiceImpl(httpClient) }
    }

    install(CallLogging)
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                ContentType.Image.WEBP -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                ContentType.Image.SVG -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<SessionToken>("SCRUM_DADDY_SESSIE")
        cookie<RedirectCookie>("SCRUM_DADDY_REDDI")
    }

    install(UserProvider)

    install(Resources)

    configureExceptionService()
    configureGroupRoutes()
    configureInviteRoutes()
    configureRouting()
    configureAuthRouting()
}