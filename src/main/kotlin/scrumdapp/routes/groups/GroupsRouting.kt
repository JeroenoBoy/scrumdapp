package scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.GroupServiceImpl
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.IsInGroup
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter

suspend fun Application.configureGroupRoutes() {

    fun checkDateSyntax(input: String): String? {
        val regex = Regex("""(\d{4})-(\d{2})-(\d{2})""")
        if (!input.matches(regex)) return null
        return input
    }

    fun parseIsoDate(input: String): LocalDate? {
        return try {
            LocalDate.parse(input)
        } catch (e: Exception) {
            null
        }
    }

    routing {
        route("/groups") {
            install(IsLoggedIn)
            route("/{groupid}") {
                install(IsInGroup) {
                    groupService = GroupServiceImpl()
                }

                get() {
                    // example url: /groups/{groupid}?date={YYYY-MM-DD}
                    val date = checkDateSyntax(call.parameters["date"] ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                }

                get("/users") {

                }

                get("/trends") {

                }

                get("/config") {

                }
            }
        }
    }
}