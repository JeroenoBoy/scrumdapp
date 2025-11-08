package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.MonthData
import com.jeroenvdg.scrumdapp.utils.scrumdappFormat
import com.jeroenvdg.scrumdapp.utils.scrumdappUrlFormat
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlin.collections.iterator

fun FlowContent.calendarContent(application: Application, group: Group, dates: MonthData) {
    card {
        div(classes="horizontal w-full space-between") {
            div {

                a(href = application.href(GroupsRouter.Group.Calendar.Content(group.id, dates.yearMonth.minusMonths(1))), classes = "btn b-none horizontal algin-center g-sm") {
                    icon(iconName = "arrow_back")
                    span { +dates.yearMonth.minusMonths(1).month.scrumdappFormat() }
                }
            }
            div(classes="flex-1") {
                form {
                }
            }
            div {
                a(href=application.href(GroupsRouter.Group.Calendar.Content(group.id, dates.yearMonth.plusMonths(1))), classes="btn b-none horizontal algin-center g-sm") {
                    span { +dates.yearMonth.plusMonths(1).month.scrumdappFormat() }
                    icon(iconName="arrow_forward")
                }
            }
        }
        table(classes="checkin-table") {
            thead {
                tr {
                    th { +"Ma" }
                    th { +"Di" }
                    th { +"Wo" }
                    th { +"Do" }
                    th { +"Vr" }
                    th(classes="fg4") { +"Za" }
                    th(classes="fg4") { +"Zo" }
                }
            }
            tbody {
                val groupedDays = dates.checkinDays.groupBy { it.date.toEpochDays() - (it.date.dayOfWeek.ordinal - 1) }
                for (groupedDay in groupedDays.toSortedMap()) {
                    tr {
                        for (day in groupedDay.value) {
                            var classes = "checkin-day"
                            if (day.hasCheckin) classes += " has-checkin"
                            if (day.date.dayOfWeek.value >= 6) classes += " weekend-day"
                            if (day.date.month != dates.yearMonth.month) classes += " other-month"
                            td(classes="text-center") {
                                a(
                                    href=application.href(GroupsRouter.Group(groupId=group.id, date=day.date.scrumdappUrlFormat())),
                                    target="_top",
                                    classes=classes
                                ) {
                                    +day.date.dayOfMonth.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}