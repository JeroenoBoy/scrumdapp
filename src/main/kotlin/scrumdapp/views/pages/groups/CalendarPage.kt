package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.MonthData
import com.jeroenvdg.scrumdapp.utils.now
import com.jeroenvdg.scrumdapp.utils.scrumdappFormat
import com.jeroenvdg.scrumdapp.utils.scrumdappUrlFormat
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.dropdown
import com.jeroenvdg.scrumdapp.views.components.dropdownItem
import com.jeroenvdg.scrumdapp.views.components.icon
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.datetime.LocalDate
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import java.time.YearMonth
import kotlin.collections.iterator

fun FlowContent.calendarContent(application: Application, group: Group, possibleMonths: List<YearMonth>, dates: MonthData) {
    val today = LocalDate.now()
    card {
        div(classes="horizontal w-full justify-between") {
            div(classes="flex-1 horizontal") {
                a(href = application.href(GroupsRouter.Group.Calendar.Content(group.id, dates.yearMonth.minusMonths(1))), classes = "btn b-none horizontal algin-center g-sm") {
                    icon(iconName = "arrow_back", classes="yellow")
                    span { +dates.yearMonth.minusMonths(1).month.scrumdappFormat() }
                }
            }
            div(classes="flex-2 horizontal align-center justify-center") {
                dropdown(dates.yearMonth.scrumdappFormat()) {
                    for (yearMonth in possibleMonths) {
                        dropdownItem(href=application.href(GroupsRouter.Group.Calendar.Content(group.id, yearMonth))) {
                            +yearMonth.scrumdappFormat()
                        }
                    }
                }
            }
            div(classes="flex-1 horizontal justify-end") {
                a(href=application.href(GroupsRouter.Group.Calendar.Content(group.id, dates.yearMonth.plusMonths(1))), classes="btn b-none horizontal algin-center g-sm") {
                    span { +dates.yearMonth.plusMonths(1).month.scrumdappFormat() }
                    icon(iconName="arrow_forward", classes="green")
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
                            if (day.date > today) classes += " not-in-reach"
                            if (day.date == today) classes += " today"
                            td(classes="text-center") {
                                if (day.date <= today) {
                                    a(
                                        href=application.href(GroupsRouter.Group(groupId=group.id, date=day.date.scrumdappUrlFormat())),
                                        target="_top",
                                        classes=classes
                                    ) {
                                        +day.date.dayOfMonth.toString()
                                    }
                                } else {
                                    span(classes=classes) { +day.date.dayOfMonth.toString() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}