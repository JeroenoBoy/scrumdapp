package com.jeroenvdg.scrumdapp.routes

import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.appSettingsPage
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing

@Resource("appsettings")
class UserSettingsRouter {
    @Resource("delete")
    class Delete(val parent: UserSettingsRouter = UserSettingsRouter()) {

    }
}

suspend fun Application.configureAppSettingsRoutes() {
    routing {
        route<UserSettingsRouter> {
            install(IsLoggedIn)
            UserSettingsRouter()
        }
    }
}

fun Route.UserSettingsRouter() {

    typedGet<UserSettingsRouter> {
        val user = call.user

        call.respondHtml {
            dashboardLayout(DashboardPageData("Gebruikers instellingen", call)) {
                appSettingsPage(application, user)
            }
        }
    }

    route<UserSettingsRouter.Delete> {
        typedPost<UserSettingsRouter.Delete> {
            val name = call.receiveParameters()["confirm_user_name"]
            val user = call.user

            return@typedPost call.respondRedirect(application.href(LogoutRouter()))
        }
    }
}