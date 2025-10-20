package com.jeroenvdg.scrumdapp.routes.groups


import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.HasCorrectPerms
import com.jeroenvdg.scrumdapp.middleware.IsInGroup
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
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

val backgrounds = listOf("1", "1_2", "2", "4", "5", "6", "6_2", "7", "7_2", "8", "9", "10", "14", "14_2", "15", "17", "18", "22", "23", "30")
val dateRegex = Regex("""(\d{4})-(\d{2})-(\d{2})""")

@Resource("groups")
class GroupsRouter {

    @Resource("{groupId}")
    class Id(val parent: GroupsRouter = GroupsRouter(), val groupId: Int, val date: String? = null) {

        @Resource("edit")
        class Edit(val parent: Id) { constructor(groupId: Int, date: String? = null): this(Id(groupId=groupId, date=date))
            @Resource("delete")
            class Delete(val parent: Edit) { constructor(groupId: Int, date: String? = null): this(Edit(Id(groupId=groupId, date=date))) }
        }

        @Resource("trends")
        class Trends(val parent: Id) { constructor(groupId: Int): this(Id(groupId=groupId))}

        @Resource("users")
        class Users(val parent: Id) { constructor(groupId: Int): this(Id(groupId=groupId))
            @Resource("delete")
            class Delete(val parent: Users) { constructor(groupId: Int): this(Users(Id(groupId=groupId))) }
        }

        @Resource("settings")
        class Settings(val parent: Id) { constructor(groupId: Int): this(Id(groupId=groupId))
            @Resource("change-name")
            class ChangeName(val parent: Settings) { constructor(groupId: Int): this(Settings(Id(groupId=groupId))) }
            @Resource("change-background")
            class ChangeBackground(val parent: Settings) { constructor(groupId: Int): this(Settings(Id(groupId=groupId))) }
            @Resource("delete")
            class Delete(val parent: Settings) { constructor(groupId: Int): this(Settings(Id(groupId=groupId))) }
        }

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
    val groupRepository = dependencies.resolve<GroupRepository>()

    routing {
        route<GroupsRouter> {
            install(IsLoggedIn)
            groupsRoutes()

            route<GroupsRouter.Id> {
                install(IsInGroup)
                groupCheckinRoutes()

                route<GroupsRouter.Id.Edit> {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    groupEditCheckinRoutes()
                }

                route<GroupsRouter.Id.Users> {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    groupUserRoutes()
                }

                route<GroupsRouter.Id.Settings> {
                    install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
                    groupSettingsRoutes()
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
//            }
//        }
    }
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