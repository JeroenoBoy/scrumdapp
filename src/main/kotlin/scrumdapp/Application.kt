package com.jeroenvdg.scrumdapp

import com.github.mustachejava.DefaultMustacheFactory
import com.jeroenvdg.scrumdapp.routes.configureRouting
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.mustache.Mustache
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    EngineMain.main(args)
    print("hi")
}

suspend fun Application.module() {
    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    install(Sessions) {
//        cookie<MySession>("SCRUM_SES") {
//            cookie.extensions["SameSite"] = "strict"
//        }
    }

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates")
    }

    dependencies {
        provide { database }
    }

    routing {
        get("/3") {
            call.respond(HttpStatusCode.OK,"Whats up")
        }
    }
    configureRouting()
}
