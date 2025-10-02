package com.jeroenvdg.scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.CheckinService
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.checkinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.editableCheckinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.groupConfigContent
import com.jeroenvdg.scrumdapp.views.pages.groups.userEditContent
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import com.scrumdapp.scrumdapp.middleware.HasCorrectPerms
import com.scrumdapp.scrumdapp.middleware.IsInGroup
import com.scrumdapp.scrumdapp.middleware.group
import com.scrumdapp.scrumdapp.middleware.groupUser
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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter

val backgrounds = listOf("1", "1_2", "2", "4", "5", "6", "7", "7_2", "8", "9", "10", "14", "14_2", "15", "17", "18", "22", "23", "30")

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
                        groupService.addGroupMember(newGroup.id, call.user, UserPermissions.LordOfScrum)
                        return@post call.respondRedirect("/groups/${newGroup.id}")
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

                    val group = call.group
                    val checkins = checkinService.getGroupCheckins(group.id, isoDate)
                    val userPerm = groupService.getGroupMemberPermissions(group.id, call.userSession.userId)

                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                            groupPage(checkins, group, userPerm) {
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

                    val group = call.group
                    val checkins = checkinService.getGroupCheckins(group.id, isoDate)
                    val userPerm = groupService.getGroupMemberPermissions(group.id, call.userSession.userId)

                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                            groupPage(checkins, group, userPerm) {
                                editableCheckinWidget(checkins, group, dateParam)
                            }
                        }
                    }
                }

                route("/users") {
                    install(HasCorrectPerms) { permissions = UserPermissions.UserManagement }

                    get {
                        val group = call.group
                        val userPerm = call.groupUser.permissions
                        val groupMembers = groupService.getGroupMembers(group.id)
                        val groupUsers = groupService.getGroupUsers(group.id)

                        call.respondHtml {
                            dashboardLayout(DashboardPageData(group.name, call)) {
                                groupPage(emptyList(), group, userPerm) {
                                    userEditContent(call.groupUser.userId, group, groupMembers, groupUsers)
                                }
                            }
                        }
                    }

                    post("/alter-users") {
                        val params = call.receiveParameters()
                        val userPerm = call.groupUser.permissions

                        for((key, value) in params.entries()) {
                            if(key.startsWith("role-")) {
                                val userId = key.removePrefix("role-").toIntOrNull()
                                val permId = value.firstOrNull()?.toIntOrNull()

                                if (userId != null && permId != null) {
                                    if (userPerm.id < permId) {
                                        val success = groupService.alterGroupMemberPerms(call.group.id, userId, UserPermissions.get(permId))
                                        if (!success) {
                                            return@post call.respondRedirect("/groups/${call.group.id}/users#alter-failed")
                                        }
                                    } else {
                                        return@post call.respondRedirect("/groups/${call.group.id}/users#alter-failed")
                                    }
                                }
                            }
                        }

                        call.respondRedirect("/groups/${call.group.id}/users#alter-success")
                    }

                    post("/delete-user") {
                        val userId = call.queryParameters["id"]?.toIntOrNull()
                        val group = call.group
                        val groupUsers = groupService.getGroupUsers(group.id)
                        val filteredList = groupUsers.filter { it.id == userId}

                        println("userid: $userId, filterlist: $filteredList")

                        if (userId == null || filteredList.isEmpty()) { return@post call.respondRedirect("/groups/${group.id}/users")}

                        // add check to confirm that person to delete isn't higher in hierarchy
                        groupService.deleteGroupMember(group.id, userId)
                        call.respondRedirect("/groups/${group.id}/users")
                    }

                    delete {

                    }
                }

                get("/trends") {

                }

                route("/config") {
                    install(HasCorrectPerms) { permissions = UserPermissions.ScrumDad }

                    get {
                        val group = call.group
                        val groupUser = call.groupUser

                        call.respondHtml {
                            dashboardLayout(DashboardPageData("Settings", call, group.bannerImage)) {
                                groupPage(emptyList(), group, groupUser.permissions) {
                                    groupConfigContent(group, groupUser, backgrounds)
                                }
                            }
                        }
                    }

                    val nameRegex = Regex("^[a-zA-Z0-9_ ]{3,50}$")
                    post("/change-name") {
                        val name = call.receiveParameters()["group_name"]
                        val group = call.group
                        if (name == null) { return@post call.respondRedirect("/groups/${group.id}/config") }
                        if (name == call.group.name) { return@post call.respondRedirect("/groups/${group.id}/config") }
                        if (!nameRegex.matches(name)) { return@post call.respondRedirect("/groups/${group.id}/config") }

                        groupService.updateGroup(group.id, name=name)
                        call.respondRedirect("/groups/${group.id}/config")
                    }

                    post("/change-image") {
                        val bannerImage = call.receiveParameters()["img"]
                        val group = call.group
                        if (!backgrounds.contains(bannerImage)) { return@post call.respondRedirect("/groups/${group.id}/config") }
                        if (bannerImage == null) { return@post call.respondRedirect("/groups/${group.id}/config") }
                        if (bannerImage == call.group.bannerImage) { return@post call.respondRedirect("/groups/${group.id}/config") }

                        groupService.updateGroup(group.id, bannerImage=bannerImage)
                        call.respondRedirect("/groups/${group.id}/config")
                    }

                    post("/delete-group") {
                        val name = call.receiveParameters()["delete_group_name"]
                        val group = call.group
                        if (name != call.group.name) { return@post call.respondRedirect("/groups/${group.id}/config#delete-failed") }

                        groupService.deleteGroup(group.id)
                        call.respondRedirect("/home")
                    }
                }
            }
        }
    }
}