package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.GroupRepository
import com.jeroenvdg.scrumdapp.services.AppException
import com.jeroenvdg.scrumdapp.utils.resolveBlocking
import com.jeroenvdg.scrumdapp.utils.route
import com.jeroenvdg.scrumdapp.utils.typedGet
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.di.*
import io.ktor.server.routing.*
import scrumdapp.services.ExportService

fun Route.groupExportUserRoutes() {
    val exportService = application.dependencies.resolveBlocking<ExportService>();
    val groupRepository = application.dependencies.resolveBlocking<GroupRepository>()

    route<GroupsRouter.Group.Export.User> {
        typedGet<GroupsRouter.Group.Export.User> { exportData ->
            val user = groupRepository.getGroupUser(exportData.groupId, exportData.userId)
            if (user == null) {
                throw AppException(404, "De gevraagde gebruiker zit niet in deze groep", "Niet Gevonden")
            }
            exportService.writeUserExport(user, call)
        }
    }
}

