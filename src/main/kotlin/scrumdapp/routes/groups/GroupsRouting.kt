package com.jeroenvdg.scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.CheckinService
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.db.UserService
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.middleware.userSession
import com.jeroenvdg.scrumdapp.models.Presence
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.services.EncryptionService
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.checkinDates
import com.jeroenvdg.scrumdapp.views.pages.groups.checkinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.editableCheckinWidget
import com.jeroenvdg.scrumdapp.views.pages.groups.groupConfigContent
import com.jeroenvdg.scrumdapp.views.pages.groups.userEditContent
import com.jeroenvdg.scrumdapp.views.pages.groups.userInviteContent
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
import io.ktor.server.plugins.origin
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

val backgrounds = listOf("1", "1_2", "2", "4", "5", "6", "7", "7_2", "8", "9", "10", "14", "14_2", "15", "17", "18", "22", "23", "30")

suspend fun Application.configureGroupRoutes() {
    val userService = dependencies.resolve<UserService>()
    val groupService = dependencies.resolve<GroupService>()
    val checkinService = dependencies.resolve<CheckinService>()
    val encryptionService = dependencies.resolve<EncryptionService>()

    val dateRegex = Regex("""(\d{4})-(\d{2})-(\d{2})""")
    fun checkDateSyntax(input: String): String {
        if (!input.matches(dateRegex)) java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return input
    }

    fun parseIsoDate(input: String): LocalDate? {
        return try {
            LocalDate.parse(input)
        } catch (e: Exception) {
            null
        }
    }

    fun generateRandomToken(length: Int): String {
        val randomGenerator = Random(System.currentTimeMillis())
        val validChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return(1..length).map { validChars.random(randomGenerator) }.joinToString("")
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
                        groupService.addGroupMember(newGroup.id, call.user.id, UserPermissions.LordOfScrum)
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
                    val checkinDates = checkinService.getCheckinDates(group.id, 10)

                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                            groupPage(checkinDates, group, userPerm) {
                                checkinWidget(checkins, group, dateParam, call.groupUser.permissions)
                            }
                        }
                    }
                }

                route("/new-checkin") {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    post {
                        val body = call.receiveParameters()
                        val date = body["date"]
                        if (date == null || !dateRegex.matches(date)) {
                            call.respondRedirect("/groups/${call.group.id}", false)
                        } else {
                            call.respondRedirect("/groups/${call.group.id}/edit?date=${date}", false)
                        }
                    }
                }

                route("/edit") {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }

                    get {
                        val dateParam = checkDateSyntax(call.parameters["date"] ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        val isoDate = parseIsoDate(dateParam)
                        if (isoDate == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid date format. Expected YYYY-MM-DD")
                            return@get
                        }

                        val group = call.group
                        val checkins = checkinService.getGroupCheckins(group.id, isoDate)
                        val userPerm = groupService.getGroupMemberPermissions(group.id, call.userSession.userId)
                        val checkinDates = checkinService.getCheckinDates(group.id, 10)

                        call.respondHtml {
                            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                                groupPage(checkinDates, group, userPerm) {
                                    editableCheckinWidget(checkins, group, dateParam)
                                }
                            }
                        }
                    }

                    post {
                        val dateParam = checkDateSyntax(call.parameters["date"] ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        val isoDate = parseIsoDate(dateParam)
                        if (isoDate == null) { return@post call.respond(HttpStatusCode.BadRequest, "Invalid date format. Expected YYYY-MM-DD") }

                        val group = call.group
                        val checkins = checkinService.getGroupCheckins(group.id, isoDate)
                        val body = call.receiveParameters()

                        for (checkin in checkins) {
                            checkin.date = isoDate
                            if (body.contains("checkin-${checkin.userId}")) {
                                checkin.checkinStars = body["checkin-${checkin.userId}"]?.toIntOrNull()
                                if (checkin.checkinStars != null) checkin.checkinStars = clamp(checkin.checkinStars!!, 0, 10)
                            }
                            if (body.contains("checkup-${checkin.userId}")) {
                                checkin.checkupStars = body["checkup-${checkin.userId}"]?.toIntOrNull()
                                if (checkin.checkupStars != null) checkin.checkupStars = clamp(checkin.checkupStars!!, 0, 10)
                            }
                            if (body.contains("presence-${checkin.userId}")) {
                                val presneceVal = body["presence-${checkin.userId}"]?.toIntOrNull()
                                checkin.presence = if (presneceVal == null) null else enumValues<Presence>()[presneceVal]
                            }
                            if (body.contains("comment-${checkin.userId}")) {
                                checkin.comment = body["comment-${checkin.userId}"]
                                if (checkin.comment.isNullOrBlank()) checkin.comment = null
                            }
                        }

                        checkinService.saveGroupCheckin(checkins)
                        call.respondRedirect("/groups/${group.id}?date=${dateParam}")
                    }
                }

                route("/users") {
                    install(HasCorrectPerms) { permissions = UserPermissions.UserManagement }

                    get {
                        val group = call.group
                        val userPerm = call.groupUser.permissions
                        val groupMembers = groupService.getGroupMembers(group.id)
                        val groupUsers = groupService.getGroupUsers(group.id)
                        val checkinDates = checkinService.getCheckinDates(group.id, 10)

                        call.respondHtml {
                            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                                groupPage(checkinDates, group, userPerm) {
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

                    post("/create-invite") {
                        val passwordRegex = Regex("^[a-zA-Z0-9_ .,#^!?><]{3,50}")
                        val group = call.group
                        val token = generateRandomToken(60)
                        val password = call.receiveParameters()["create_group_invite"]

                        if (password.isNullOrBlank() || !passwordRegex.matches(password)) {
                            return@post call.respondRedirect("/groups/${group.id}/users#create-invite")
                        } else {
                            try {
                                groupService.createGroupInvite(group.id, token, encryptionService.hashValue(password))
                            } catch (e: Exception) {
                                // Throw error modal
                            }
                        }

                        val origin = call.request.origin.serverHost
                        val url = "https://$origin/invitations?token=$token"

                        call.respondHtml {
                            dashboardLayout(DashboardPageData(group.name, call)) {
                                groupPage(emptyList(), group, call.groupUser.permissions) {
                                    userInviteContent(group, url)
                                }
                            }
                        }
                    }
                }

                get("/trends") {
                    val group = call.group
                    val userPerm = call.groupUser.permissions
                    val checkinDates = checkinService.getCheckinDates(group.id, 10)

                    call.respondHtml {
                        dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                            groupPage(checkinDates, group, userPerm) {
                            }
                        }
                    }
                }

                route("/config") {
                    install(HasCorrectPerms) { permissions = UserPermissions.ScrumDad }

                    get {
                        val group = call.group
                        val groupUser = call.groupUser
                        val checkinDates = checkinService.getCheckinDates(group.id, 10)

                        call.respondHtml {
                            dashboardLayout(DashboardPageData("Settings", call, group.bannerImage)) {
                                groupPage(checkinDates, group, groupUser.permissions) {
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

fun clamp(value: Int, min: Int, max: Int): Int {
    if (value > max) return max;
    if (value < min) return min;
    return value
}