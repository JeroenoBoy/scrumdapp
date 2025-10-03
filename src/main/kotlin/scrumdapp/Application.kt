package com.jeroenvdg.scrumdapp

import com.jeroenvdg.scrumdapp.Database.initializeDatabase
import com.jeroenvdg.scrumdapp.db.*
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.middleware.UserProvider
import com.jeroenvdg.scrumdapp.models.GroupsTable
import com.jeroenvdg.scrumdapp.models.UserTable
import com.jeroenvdg.scrumdapp.routes.SessionToken
import com.jeroenvdg.scrumdapp.routes.configureAuthRouting
import com.jeroenvdg.scrumdapp.routes.configureRouting
import com.jeroenvdg.scrumdapp.routes.groups.configureGroupRoutes
import com.jeroenvdg.scrumdapp.routes.groups.configureInviteRoutes
import com.jeroenvdg.scrumdapp.services.DotenvService
import com.jeroenvdg.scrumdapp.services.EncryptionServiceImpl
import com.jeroenvdg.scrumdapp.services.EnvironmentService
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
    val userService = UserRepositoryImpl()
    val sessionService = SessionRepositoryImpl()
    val groupService = GroupRepositoryImpl()
    val checkinService = CheckinRepositoryImpl()

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

    install(UserProvider) {
        this.userRepository = userService
        this.sessionRepository = sessionService
    }

    dependencies {
        provide { database }
        provide { UserTable(database) }
        provide { GroupsTable(database) }
        provide { httpClient }
        provide { encryptionService }
        provide<UserRepository> { userService }
        provide<GroupRepository> { groupService }
        provide<CheckinRepository> { checkinService }
        provide<SessionRepository> { sessionService }
        provide<DiscordService> { DiscordServiceImpl(httpClient) }
    }

    configureGroupRoutes()
    configureInviteRoutes()
    configureRouting()
    configureAuthRouting()
}