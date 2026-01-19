package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.aboutPage
import com.jeroenvdg.scrumdapp.views.pages.homePage
import com.jeroenvdg.scrumdapp.views.pages.privacyPage
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.di.*
import io.ktor.server.resources.href
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Resource("home")
class HomeRouter()

@Resource("about")
class AboutRouter()

@Resource("privacy")
class PrivacyRouter()
suspend fun Application.configureRouting() {
    val users = dependencies.resolve<UserRepository>()
    val groups = dependencies.resolve<GroupRepository>()

    routing {
        get("/") {
            call.respondRedirect(href(LoginRouter()))
        }

        route<HomeRouter> {
            install(IsLoggedIn)
            get {
                val groups = groups.getUserGroups(call.userSession.userId)
                call.respondHtml {
                    dashboardLayout(DashboardPageData("Home", call)) {
                        homePage(application, groups)
                    }
                }
            }
        }

        route<AboutRouter> {
            install(IsLoggedIn)
            get {
                call.respondHtml {
                    dashboardLayout(DashboardPageData("About", call)) {
                        aboutPage()
                    }
                }
            }
        }

        route<PrivacyRouter> {
            install(IsLoggedIn) {
                get {
                    call.respondHtml {
                        dashboardLayout(DashboardPageData("Privacy Statement", call)) {
                            privacyPage()
                        }
                    }
                }
            }
        }

        staticResources("/static", "static")
    }
}
