package com.jeroenvdg.scrumdapp.routes.groups.trends

import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.utils.typedGet
import io.ktor.server.application.Application
import io.ktor.server.routing.Route

fun Route.trendsRoutes(application: Application) {
    typedGet<GroupsRouter.Group.Trends> { trends ->

    }
}