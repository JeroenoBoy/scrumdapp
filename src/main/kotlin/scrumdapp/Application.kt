package com.jeroenvdg.scrumdapp

import com.github.mustachejava.DefaultMustacheFactory
import com.jeroenvdg.scrumdapp.models.UserTable
import com.jeroenvdg.scrumdapp.routes.configureAuthRouting
import com.jeroenvdg.scrumdapp.routes.configureRouting
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordServiceImpl
import com.jeroenvdg.scrumdapp.Database.initializeDatabase
import com.jeroenvdg.scrumdapp.db.CheckinService
import com.jeroenvdg.scrumdapp.db.CheckinServiceImpl
import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.db.GroupServiceImpl
import com.jeroenvdg.scrumdapp.db.SessionService
import com.jeroenvdg.scrumdapp.db.SessionServiceImpl
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.db.UserServiceImpl
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.middleware.UserProvider
import com.jeroenvdg.scrumdapp.models.GroupsTable
import com.jeroenvdg.scrumdapp.routes.SessionToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.mustache.Mustache
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import scrumdapp.routes.groups.configureGroupRoutes
import scrumdapp.services.DotenvService
import scrumdapp.services.EnvironmentService

fun main(args: Array<String>) {
    println("Starting Scrumdapp")
    EngineMain.main(args)
}

suspend fun Application.module() {
    val env = DotenvService()
    val httpClient = HttpClient(CIO) { }
    dependencies { provide<EnvironmentService> { env } }
    val database = initializeDatabase()
    val userService = UserServiceImpl()
    val sessionService = SessionServiceImpl()
    val groupService = GroupServiceImpl()
    val checkinService = CheckinServiceImpl()

    install(CallLogging)

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<SessionToken>("SCRUM_DADDY_SESSIE")
        cookie<RedirectCookie>("SCRUM_DADDY_REDDI")
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    install(UserProvider) {
        this.userService = userService
        this.sessionService = sessionService
    }

    dependencies {
        provide { database }
        provide { UserTable(database) }
        provide { GroupsTable(database) }
        provide { httpClient }
        provide<UserService> { userService }
        provide<GroupService> { groupService }
        provide<CheckinService> { checkinService }
        provide<SessionService> { sessionService }
        provide<DiscordService> { DiscordServiceImpl(httpClient) }
    }

    configureGroupRoutes()
    configureRouting()
    configureAuthRouting()
}