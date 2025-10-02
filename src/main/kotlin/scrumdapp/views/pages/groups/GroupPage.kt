package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.datetime.LocalDate
import kotlinx.html.*

inline fun FlowContent.groupPage(checkins: List<LocalDate>, group: Group, userPermissions: UserPermissions, crossinline block: MAIN.() -> Unit = {}) {
    h1 {+group.name}
    div(classes="horizontal g-lg mb-lg") {
        aside(classes="vertical relative") {
//            if (userPermissions.id <= UserPermissions.User.id) {
                div(classes="vertical g-lg sticky") { style = "top: 4em"
                    div(classes="card vertical g-md") {
                        i(classes="px-lg text-center") {+"Pagina's"}
                        hr {}
                        a(href="/groups/${group.id}/trends", classes="btn b-none px-lg") {
                            icon(iconName="bar_chart", classes="yellow")
                            +"Trends"
                        }
                        if (userPermissions.id <= UserPermissions.UserManagement.id) {
                            a(href="/groups/${group.id}/users", classes="btn b-none px-lg") {
                                icon(iconName="groups", classes="blue")
                                +"Gebruikers"
                            }
                        }
                        if (userPermissions.id <= UserPermissions.ScrumDad.id) {
                            a(href="/groups/${group.id}/config", classes="btn b-none px-lg") {
                                icon(iconName="settings", classes="purple")
                                +"Instellingen"
                            }
                        }
                    }
                    checkinDates(checkins, group, userPermissions)
                }
//            }
        }
        div(classes="flex-1") {
            main(classes="card flex-1 vertical g-md") { id="group-content"
                block()
            }
        }
    }
}

fun FlowContent.checkinDates(dates: List<LocalDate>, group: Group, perms: UserPermissions) {
    div(classes="card vertical g-md") {
        if (perms.id <= UserPermissions.CheckinManagement.id) {
            form(classes="horizontal justify-between items-center") {
                i(classes="px-lg my-auto") {+"Data" }
                input(classes="btn btn-red", type=InputType.submit) {value="+"}
            }
        }
        hr {}
        div(classes="vertical g-md") { style="max-height: 15em; overflow-y: scroll"
            for (date in dates) {
                val y = date.year.toString().padStart(4, '0')
                val m = date.monthNumber.toString().padStart(2, '0')
                val d = date.dayOfMonth.toString().padStart(2, '0')
                a(classes = "btn b-none px-lg text-center", href="/groups/${group.id}?date=${"$y-$m-$d"}") {
                    span(classes="gray") { +"$y / $m / $d" }
                }
            }
        }
    }
}

