package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.mainLayout
import com.jeroenvdg.scrumdapp.views.pages.aboutPage
import com.jeroenvdg.scrumdapp.views.pages.homePage
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.di.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.h1
import kotlinx.html.img
import kotlinx.html.p
import kotlinx.html.strong


suspend fun Application.configureRouting() {
    val users = dependencies.resolve<UserRepository>()
    val groups = dependencies.resolve<GroupRepository>()

    routing {
        get("/") {
            call.respondRedirect("/login")
        }

        route("/home") {
            install(IsLoggedIn)
            get {
                val groups = groups.getUserGroups(call.userSession.userId)
                call.respondHtml {
                    dashboardLayout(DashboardPageData("Home", call)) {
                        homePage(groups)
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
