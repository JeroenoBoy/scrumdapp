package scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureGroupRoutes() {
    routing {
        route("/groups") {
            install(IsLoggedIn)
            get("/new") {}
            route("/{id}") {
            }
        }
    }
}