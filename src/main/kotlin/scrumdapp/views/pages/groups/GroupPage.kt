package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.ExceptionContent
import com.jeroenvdg.scrumdapp.utils.scrumdappFormat
import com.jeroenvdg.scrumdapp.utils.scrumdappUrlFormat
import com.jeroenvdg.scrumdapp.views.components.errorPopup
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.datetime.LocalDate
import kotlinx.html.*
import java.time.format.DateTimeFormatter
import kotlin.random.Random

inline fun FlowContent.groupPage(application: Application, checkins: List<LocalDate>, group: Group, perms: UserPermissions, exception: ExceptionContent? = null, crossinline block: MAIN.() -> Unit = {}) {
    val rng = Random.nextInt(9999999)
    h1 { +group.name }
    div(classes = "horizontal g-lg mb-lg") {
        aside(classes = "vertical relative") {
            div(classes = "vertical g-lg sticky") {
                style = "top: 4em"
                div(classes = "card vertical g-md") {
                    i(classes = "px-lg text-center") { +"Pagina's" }
                    hr {}
                    a(href=application.href(GroupsRouter.Group.Calendar(group.id)), classes="btn b-none px-lg") {
                        icon(iconName="calendar_month", classes="aqua text-lg")
                        i(classes="my-auto") { +"Kalender" }
                    }
                    a(href=application.href(GroupsRouter.Group.Trends(group.id)), classes = "btn b-none px-lg") {
                        icon(iconName = "bar_chart", classes = "yellow")
                        +"Trends"
                    }
                    a(href=application.href(GroupsRouter.Group.Notes(group.id)), classes="btn b-none px-lg") {
                        icon(iconName="notes", classes="red")
                        +"Notities"
                    }
                    if (perms.id <= UserPermissions.UserManagement.id) {
                        a(href = application.href(GroupsRouter.Group.Users(group.id)), classes = "btn b-none px-lg") {
                            icon(iconName = "groups", classes = "blue")
                            +"Gebruikers"
                        }
                    }
                    if (perms.id <= UserPermissions.ScrumDad.id) {
                        a(href = application.href(GroupsRouter.Group.Settings(group.id)), classes = "btn b-none px-lg") {
                            icon(iconName = "settings", classes = "purple")
                            +"Instellingen"
                        }
                    }
                }
                checkinDates(application, checkins, group, perms, rng)
            }
        }
        div(classes="flex-1") {
            main(classes="flex-1 vertical g-md") { id="group-content"
                block()
            }
        }
    }
    if (perms.id <= UserPermissions.CheckinManagement.id) {
        modal("create-checkin-$rng") {
            form(action = application.href(GroupsRouter.Group.Edit(group.id)), method = FormMethod.get, classes = "vertical g-md") {
                h2 { +"Nieuwe Check-in" }

                div(classes = "input-group") {
                    label(classes = "input-label") { htmlFor = "date"; +"Check-in datum" }
                    div(classes = "flex-1")
                    input(type = InputType.date, name = "date", classes = "btn") {
                        val today = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        value = today
                        max = today
                    }
                }

                div(classes = "horizontal g-md justify-end") {
                    a(href = "#", classes = "btn") {
                        icon(iconName = "undo", classes = "gray")
                        +"Terug"
                    }
                    div(classes = "hacky-icon") {
                        icon(iconName = "add", classes = "bg-hard")
                        input(type = InputType.submit, classes = "btn btn-blue") { value = "Maak Check-in" }
                    }
                }
            }
        }
    }

    if (exception != null) {
        errorPopup(exception)
    }
}


fun FlowContent.checkinDates(application: Application, dates: List<LocalDate>, group: Group, perms: UserPermissions, rng: Int) {
    div(classes = "card vertical g-md pr-0 card-sb") {
        div(classes = "vertical g-md pr-lg") {
            a(classes = "btn b-none px-lg text-center", href = application.href(GroupsRouter.Group(groupId = group.id))) {
                i { +"Vandaag" }
            }
            hr {}
        }
        div(classes = "vertical g-md pr-lg") {
            style = "max-height: 15em;overflow-y: scroll;"
            for (date in dates) {
                a(classes = "btn b-none px-lg text-center", href = application.href(GroupsRouter.Group(groupId = group.id, date = date.scrumdappUrlFormat()))) {
                    span(classes = "gray") { +date.scrumdappFormat() }
                }
            }
        }
    }
}