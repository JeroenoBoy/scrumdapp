package scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.CheckinRepository
import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.HasCorrectPerms
import com.jeroenvdg.scrumdapp.middleware.group
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import com.jeroenvdg.scrumdapp.utils.typedPost
import com.jeroenvdg.scrumdapp.views.DashboardPageData
import com.jeroenvdg.scrumdapp.views.dashboardLayout
import com.jeroenvdg.scrumdapp.views.pages.groups.groupPage
import io.ktor.resources.href
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.plugins.di.getBlocking
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.href
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import scrumdapp.views.pages.groups.notesGroupPageContent
import scrumdapp.views.pages.groups.notesGroupPageEditContent

fun Route.groupNoteRoutes() {
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>();
    val checkinRepository = application.dependencies.resolveBlocking<CheckinRepository>()

    typedGet<GroupsRouter.Group.Notes> { groupNotes ->
        val group = call.group
        val user = call.groupUser
        val checkinDates = checkinRepository.getRecentCheckinDates(group.id)
        val notes = groupRepository.getGroupNotes(group.id)

        call.respondHtml {
            dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                groupPage(application, checkinDates, group, call.groupUser.permissions) {
                    notesGroupPageContent(application, group.id, notes, user.permissions)
                }
            }
        }
    }

    route<GroupsRouter.Group.Notes.Edit> {
        install(HasCorrectPerms) { permissions = UserPermissions.CheckinManagement }
        typedGet<GroupsRouter.Group.Notes.Edit> {
            val group = call.group
            val user = call.groupUser
            val checkinDates = checkinRepository.getRecentCheckinDates(group.id)
            val notes = groupRepository.getGroupNotes(group.id)

            call.respondHtml {
                dashboardLayout(DashboardPageData(group.name, call, group.bannerImage)) {
                    groupPage(application, checkinDates, group, call.groupUser.permissions) {
                        notesGroupPageEditContent(application, group.id, notes, user.permissions)
                    }
                }
            }
        }

        typedPost<GroupsRouter.Group.Notes.Edit> {
            val group = call.group
            val notes = call.receiveParameters()["notes"]
            groupRepository.saveGroupNotes(group.id, notes)
            call.respondRedirect(application.href(GroupsRouter.Group.Notes(group.id)))
        }
    }
}