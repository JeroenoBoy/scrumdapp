package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.middileware.Make404
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsLoggedOut
import com.jeroenvdg.scrumdapp.models.UserTable
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.mainLayout
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.*
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.Sessions
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.strong
import org.jetbrains.exposed.sql.Database

data class MustacheUser(val id: Int, val name: String)

suspend fun Application.configureRouting() {
    val users = dependencies.resolve<UserTable>()

    routing {
        get("/") {
            call.respond(MustacheContent("index.hbs", mapOf("user" to MustacheUser(1, "user1"))))
        }

        route("/home") {
            install(IsLoggedIn)
            get {
                val session = call.tryGetUserSession() ?: return@get
                call.respondHtml {
                    mainLayout(PageData("Home")) {
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
        }

        route("/epic") {
            install(IsLoggedOut)
            get {
                val session = call.tryGetUserSession() ?: return@get
                call.respondHtml {
                    mainLayout(PageData("Home")) {
                        h1 {
                            +"Scrumdapp"
                        }
                        p {
                            +"Super epic story, innit "
                            strong { +session.userData.name }
                            +"?"
                        }
                        if (session.userData.avatar != null) {
                            img(alt="User profile picture", "https://cdn.discordapp.com/avatars/${session.userData.discordId}/${session.userData.avatar}.png")
                        }
                    }
                }
            }
        }

        staticResources("/static", "static")
    }
}
