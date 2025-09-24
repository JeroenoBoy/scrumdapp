package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.middileware.Make404
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsLoggedOut
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.models.UserTable
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.mainLayout
import com.jeroenvdg.scrumdapp.views.pages.aboutPage
import com.jeroenvdg.scrumdapp.views.pages.homePage
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
                call.respondHtml {
                    dashboardLayout(DashboardPageData("Home", call)) {
                        homePage()
                    }
                }
            }
        }

        route("/about") {
            install(IsLoggedIn)
            get {
                call.respondHtml {
                    dashboardLayout(DashboardPageData("About", call)) {
                        aboutPage()
                    }
                }
            }
        }

        route("/epic") {
            install(IsLoggedIn)
            get {
                val user = call.user
                call.respondHtml {
                    mainLayout(PageData("Home")) {
                        h1 {
                            +"Scrumdapp"
                        }
                        p {
                            +"Super epic story, innit "
                            strong { +user.name }
                            +"?"
                        }
                        if (user.profileImage.isNotEmpty()) {
                            img(alt="User profile picture", user.profileImage)
                        }
                    }
                }
            }
        }

        staticResources("/static", "static")
    }
}
