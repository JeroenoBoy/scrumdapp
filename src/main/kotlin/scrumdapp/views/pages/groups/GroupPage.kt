package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.datetime.LocalDate
import kotlinx.html.*
import java.time.format.DateTimeFormatter
import kotlin.random.Random

inline fun FlowContent.groupPage(checkins: List<LocalDate>, group: Group, perms: UserPermissions, crossinline block: MAIN.() -> Unit = {}) {
    var rng = Random.nextInt(9999999)
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
                        if (perms.id <= UserPermissions.UserManagement.id) {
                            a(href="/groups/${group.id}/users", classes="btn b-none px-lg") {
                                icon(iconName="groups", classes="blue")
                                +"Gebruikers"
                            }
                        }
                        if (perms.id <= UserPermissions.ScrumDad.id) {
                            a(href="/groups/${group.id}/config", classes="btn b-none px-lg") {
                                icon(iconName="settings", classes="purple")
                                +"Instellingen"
                            }
                        }
                    }
                    checkinDates(checkins, group, perms, rng)
                }
//            }
        }
        div(classes="flex-1") {
            main(classes="card flex-1 vertical g-md") { id="group-content"
                block()
            }
        }
    }
    if (perms.id <= UserPermissions.CheckinManagement.id) {
        modal("create-checkin-$rng") {
            form(action="/groups/${group.id}/new-checkin", method=FormMethod.post, classes="vertical g-md") {
                h2 { +"Nieuwe Check-in" }

                div(classes="input-group") {
                    label(classes="input-label") { htmlFor="date"; +"Check-in datum" }
                    div(classes="flex-1")
                    input(type=InputType.date, name="date", classes="btn") {
                        val today = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        value = today
                        max = today
                    }
                }

                div(classes="horizontal g-md justify-end") {
                    a(href="#", classes="btn") {
                        icon(iconName="undo", classes="gray")
                        +"Terug"
                    }
                    div(classes="hacky-icon") {
                        icon(iconName="add", classes="bg-hard")
                        input(type=InputType.submit, classes="btn btn-blue") { value="Maak Check-in" }
                    }
                }
            }
        }
    }
}

fun FlowContent.checkinDates(dates: List<LocalDate>, group: Group, perms: UserPermissions, rng: Int) {
    div(classes="card vertical g-md") {
        if (perms.id <= UserPermissions.CheckinManagement.id) {
            div(classes="horizontal justify-between items-center") {
                i(classes="px-lg my-auto") {+"Data" }
                a(href="#create-checkin-$rng", classes="btn btn-red") {
                    +"+"
                }
            }
            hr {}
        }
        div(classes="vertical g-md") { style="max-height: 15em;overflow-y: scroll;padding:2px"
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

