package scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.GroupServiceImpl
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsInGroup
import com.jeroenvdg.scrumdapp.middleware.HasCorrectPerms
import com.jeroenvdg.scrumdapp.models.UserPermissions
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

suspend fun Application.configureGroupRoutes() {
    routing {
        route("/groups") {
            install(IsLoggedIn)
            route("/{groupid}") {
                install(IsInGroup) {
                    groupService = GroupServiceImpl()
                }

                get() {

                }

                get("/users") {

                }

                get("/trends") {

                }

                get("/config") {

                }

                get("/{date}") {
                    install(HasCorrectPerms) {
                        permissions = UserPermissions.ScrumDad
                    }


                }
            }
        }
    }
}