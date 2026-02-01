package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.middleware.ComparePermissions
import com.jeroenvdg.scrumdapp.middleware.groupUser
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.services.AppException
import com.jeroenvdg.scrumdapp.services.NotAuthorizedException
import com.jeroenvdg.scrumdapp.services.NotFoundException
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import scrumdapp.services.ExportService

fun Route.groupExportUserRoutes() {
    val exportService = application.dependencies.resolveBlocking<ExportService>();
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    route<GroupsRouter.Group.Export.User> {
        typedGet<GroupsRouter.Group.Export.User> { exportData ->
            val groupUser = call.groupUser
            val requestedUser = groupRepository.getGroupUser(exportData.groupId, exportData.userId)

            if (!UserPermissions.canExportPresence(groupUser.permissions, groupUser == requestedUser)) {
                throw NotAuthorizedException("Alleen coaches mogen dit doen")
            }

            if (requestedUser == null) {
                throw AppException(404, "De gevraagde gebruiker zit niet in deze groep", "Niet Gevonden")
            }

            exportService.writeUserExport(requestedUser, call)
        }
    }
}

