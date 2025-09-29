package scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.CheckinService
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.db.GroupServiceImpl
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.checkinWidget
import com.jeroenvdg.scrumdapp.views.pages.editableCheckinWidget
import com.jeroenvdg.scrumdapp.views.pages.groupPage
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import scrumdapp.middleware.HasCorrectPerms
import scrumdapp.middleware.IsInGroup
import java.time.format.DateTimeFormatter

suspend fun Application.configureGroupRoutes() {
    val userService = dependencies.resolve<UserService>()
    val groupService = dependencies.resolve<GroupService>()
    val checkinService = dependencies.resolve<CheckinService>()

    fun checkDateSyntax(input: String): String {
        val regex = Regex("""(\d{4})-(\d{2})-(\d{2})""")
        if (!input.matches(regex)) java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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

            get {
                call.respondRedirect("/home", false)
            }

            post {
                try {
                    val groupName = call.receiveParameters()["group_name"].toString()
                    val newGroup = groupService.createGroup(Group(0, groupName, null))
                    if (newGroup != null) {
                        groupService.addGroupMember(newGroup, call.user, UserPermissions.LordOfScrum)
                        call.respondRedirect("/groups/${newGroup.id}/config")
                    }
                    call.respond(HttpStatusCode.InternalServerError)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            route("/{groupid}") {
                install(IsInGroup) { this.groupService = groupService }

                get {
                    // example url: /groups/{groupid}?date={YYYY-MM-DD}
                    val dateParam = checkDateSyntax(call.parameters["date"] ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    val isoDate = parseIsoDate(dateParam)
                    if (isoDate == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid date format. Expected YYYY-MM-DD")
                        return@get
                    }
                    val group = groupService.getGroup(call.parameters["groupid"]?.toInt() ?: -1) // To Do: Make this a bit cleaner
                    if (group == null) {
                        call.respond(HttpStatusCode.BadRequest, "Unknown or invalid group id")
                        return@get
                    }

                    val checkins = checkinService.getGroupCheckins(group, isoDate)
                    //val userPerm = groups.getGroupMemberPermissions(group, call.userSession.id)
                    //println("userPerm: ${userPerm.displayName}")
                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call)) {
                            groupPage(checkins, group, UserPermissions.ScrumDad) {
                                checkinWidget(checkins, group, dateParam, UserPermissions.CheckinManagement)
                            }
                        }
                    }
                }

                get("/edit") {
                    val dateParam = checkDateSyntax(call.parameters["date"] ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    val isoDate = parseIsoDate(dateParam)
                    if (isoDate == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid date format. Expected YYYY-MM-DD")
                        return@get
                    }
                    val group = groupService.getGroup(call.parameters["groupid"]?.toInt() ?: -1) // To Do: Make this a bit cleaner
                    if (group == null) {
                        call.respond(HttpStatusCode.BadRequest, "Unknown or invalid group id")
                        return@get
                    }

                    val checkins = checkinService.getGroupCheckins(group, isoDate)
                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call)) {
                            groupPage(checkins, group, UserPermissions.ScrumDad) {
                                editableCheckinWidget(checkins, group, dateParam)
                            }
                        }
                    }
                }

                route("/users") {
                    install(HasCorrectPerms) { permissions = UserPermissions.UserManagement }

                    get {

                    }

                    put {

                    }

                    delete {

                    }
                }

                get("/trends") {

                }

                route("/config") {
                    install(HasCorrectPerms) { permissions = UserPermissions.ScrumDad }

                    get {

                    }

                    put {

                    }
                }
            }
        }
    }
}