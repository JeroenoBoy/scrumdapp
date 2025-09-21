package com.jeroenvdg.scrumdapp

import com.github.mustachejava.DefaultMustacheFactory
import com.jeroenvdg.scrumdapp.middleware.RedirectCookie
import com.jeroenvdg.scrumdapp.models.UserTable
import com.jeroenvdg.scrumdapp.routes.UserSession
import com.jeroenvdg.scrumdapp.routes.authRouting
import com.jeroenvdg.scrumdapp.routes.configureRouting
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordService
import com.jeroenvdg.scrumdapp.services.oauth2.discord.DiscordServiceImpl
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.application.install
import io.ktor.server.mustache.Mustache
import io.ktor.server.netty.EngineMain
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    println("Starting Scrumdapp")
    EngineMain.main(args)
}

suspend fun Application.module() {
    val dotenv = Dotenv.load()
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )
    val httpClient = HttpClient(CIO) { }

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<UserSession>("SCRUM_DADDY_SESSIE")
        cookie<RedirectCookie>("SCRUM_DADDY_REDDI")
//        cookie<MySession>("SCRUM_SES") {
//            cookie.extensions["SameSite"] = "strict"
//        }
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    dependencies {
        provide { database }
        provide { UserTable(database) }
        provide { dotenv }
        provide { httpClient }
        provide<DiscordService> { DiscordServiceImpl(httpClient) }
    }

    routing {
        get("/3") {
            call.respond(HttpStatusCode.OK,"Whats up")
        }
    }
    configureRouting()
    authRouting()
}