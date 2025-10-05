package com.jeroenvdg.scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.db.UserRepository
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.services.CheckinService
import com.jeroenvdg.scrumdapp.services.EncryptionService
import com.jeroenvdg.scrumdapp.services.UserService
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.scrumdapp.scrumdapp.middleware.HasCorrectPerms
import com.scrumdapp.scrumdapp.middleware.IsInGroup
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.serialization.JsonConvertException
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.routing
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

val backgrounds = listOf("1", "1_2", "2", "4", "5", "6", "6_2", "7", "7_2", "8", "9", "10", "14", "14_2", "15", "17", "18", "22", "23", "30")
val dateRegex = Regex("""(\d{4})-(\d{2})-(\d{2})""")

@Resource("/groups")
class Groups() {
    @Resource("{groupId}")
    class Id(val parent: Groups = Groups(), val groupId: Int, val date: String? = null) {
        @Resource("edit")
        class Edit(val parent: Id)
        @Resource("users")
        class Users(val parent: Id) { constructor(groupId: Int): this(Id(groupId=groupId))
            @Resource("delete")
            class Delete(val parent: Users) { constructor(groupId: Int): this(Users(Id(groupId=groupId))) }
        }
        @Resource("trends")
        class Trends(val parent: Id) { constructor(groupId: Int): this(Id(groupId=groupId))}

        fun getDateParam(): String {
            return checkDateSyntax(date ?: java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }

        fun getIsoDateParam(): LocalDate {
            return parseIsoDate(getDateParam()) ?:
                throw Exception("Invalid date param, expected yyyy-mm-dd")
        }
    }
}

fun Route.groupsRoutes() {
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    get { ->
        call.respondRedirect("/home")
    }

    post {
        try {
            val groupName = call.receiveParameters()["group_name"].toString()
            val newGroup = groupRepository.createGroup(Group(0, groupName, null))
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            groupRepository.addGroupMember(newGroup.id, call.user.id, UserPermissions.LordOfScrum)
            call.respondRedirect("/groups/${newGroup.id}")
        } catch (ex: IllegalStateException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: JsonConvertException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}

suspend fun Application.configureGroupRoutes() {
    val userRepository = dependencies.resolve<UserRepository>()
    val groupRepository = dependencies.resolve<GroupRepository>()
    val checkinRepository = dependencies.resolve<CheckinRepository>()
    val encryptionService = dependencies.resolve<EncryptionService>()
    val userService = UserService(groupRepository, checkinRepository, encryptionService)
    val checkinService = CheckinService(checkinRepository, groupRepository)

    routing {
        route<Groups> {
            install(IsLoggedIn)
            groupsRoutes()

            route<Groups.Id> {
                install(IsInGroup) { this.groupRepository = groupRepository }
                groupCheckinRoutes()

                route<Groups.Id.Edit> {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    groupEditCheckinRoutes()
                }

                route<Groups.Id.Users> {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    groupUserRoutes()
                }
            }
        }
//        route("/groups") {
//            install(IsLoggedIn)
//
//                TODO: remove this route, seems useless to me now
//                route("/new-checkin") {
//                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
//                    post {
//                        val body = call.receiveParameters()
//                        val date = body["date"]
//                        if (date == null || !dateRegex.matches(date)) {
//                            call.respondRedirect("/groups/${call.group.id}", false)
//                        } else {
//                            call.respondRedirect("/groups/${call.group.id}/edit?date=${date}", false)
//                        }
//                    }
//                }
//
//
//                get("/trends") {
//                    val group = call.group
//                    val userPerm = call.groupUser.permissions
//                    val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
//
//                    call.respondHtml {
//                        dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
//                            groupPage(checkinDates, group, userPerm) {
//                            }
//                        }
//                    }
//                }
//
//                route("/config") {
//                    install(HasCorrectPerms) { permissions = UserPermissions.ScrumDad }
//
//                    get {
//                        val group = call.group
//                        val groupUser = call.groupUser
//                        val checkinDates = checkinRepository.getCheckinDates(group.id, 10)
//
//                        call.respondHtml {
//                            dashboardLayout(DashboardPageData("Settings", call, group.bannerImage)) {
//                                groupPage(checkinDates, group, groupUser.permissions) {
//                                    groupConfigContent(group, groupUser, backgrounds)
//                                }
//                            }
//                        }
//                    }
//
//                    val nameRegex = Regex("^[a-zA-Z0-9_ ]{3,50}$")
//                    post("/change-name") {
//                        val name = call.receiveParameters()["group_name"]
//                        val group = call.group
//                        if (name == null) { return@post call.respondRedirect("/groups/${group.id}/config") }
//                        if (name == call.group.name) { return@post call.respondRedirect("/groups/${group.id}/config") }
//                        if (!nameRegex.matches(name)) { return@post call.respondRedirect("/groups/${group.id}/config") }
//
//                        groupRepository.updateGroup(group.id, name=name)
//                        call.respondRedirect("/groups/${group.id}/config")
//                    }
//
//                    post("/change-image") {
//                        val bannerImage = call.receiveParameters()["img"]
//                        val group = call.group
//                        if (!backgrounds.contains(bannerImage)) { return@post call.respondRedirect("/groups/${group.id}/config") }
//                        if (bannerImage == null) { return@post call.respondRedirect("/groups/${group.id}/config") }
//                        if (bannerImage == call.group.bannerImage) { return@post call.respondRedirect("/groups/${group.id}/config") }
//
//                        groupRepository.updateGroup(group.id, bannerImage=bannerImage)
//                        call.respondRedirect("/groups/${group.id}/config")
//                    }
//
//                    post("/delete-group") {
//                        val name = call.receiveParameters()["delete_group_name"]
//                        val group = call.group
//                        if (name != call.group.name) { return@post call.respondRedirect("/groups/${group.id}/config#delete-failed") }
//
//                        groupRepository.deleteGroup(group.id)
//                        call.respondRedirect("/home")
//                    }
//                }
//            }
//        }
    }
}

fun generateRandomToken(length: Int): String {
    val randomGenerator = Random(System.currentTimeMillis())
    val validChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return(1..length).map { validChars.random(randomGenerator) }.joinToString("")
}

fun parseIsoDate(input: String): LocalDate? {
    return try {
        LocalDate.parse(input)
    } catch (e: Exception) {
        null
    }
}

fun checkDateSyntax(input: String): String {
    if (!input.matches(dateRegex)) java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    return input
}


fun clamp(value: Int, min: Int, max: Int): Int {
    if (value > max) return max
    if (value < min) return min
    return value
}