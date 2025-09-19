package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.middileware.Make404
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.Sessions
import org.jetbrains.exposed.sql.Database

data class MustacheUser(val id: Int, val name: String)

suspend fun Application.configureRouting() {
    val db = dependencies.resolve<Database>()

    routing {
        get("/1") {
            print("yo")
            call.respond(MustacheContent("index.hbs", mapOf("user" to MustacheUser(1, "user1"))))
        }
        staticResources("/static", "static")

        route("/2") {
            install(Make404)
            get {
                call.respond(MustacheContent("index.hbs", mapOf("user" to MustacheUser(1, "user1"))))
            }
        }
    }
}
