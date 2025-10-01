package com.jeroenvdg.scrumdapp.views.pages.groups

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.User
import com.jeroenvdg.scrumdapp.db.UserGroup
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.views.components.icon
import com.jeroenvdg.scrumdapp.views.components.modal
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.MAIN
import kotlinx.html.table
import kotlinx.html.thead
import kotlinx.html.tbody
import kotlinx.html.tr
import kotlinx.html.th
import kotlinx.html.td
import kotlinx.html.span
import kotlinx.html.a
import kotlinx.html.aside
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.main
import kotlinx.html.option
import kotlinx.html.p
import kotlinx.html.select
import kotlinx.html.style
import kotlinx.html.textArea

val checkinColorMap = listOf("red", "orange-dim", "orange", "yellow-dim", "yellow", "green-dim", "green", "aqua", "blue", "blue-dim", "gray")

inline fun FlowContent.groupPage(checkins: List<Checkin>, group: Group, userPermissions: UserPermissions, crossinline block: MAIN.() -> Unit = {}) {
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
                    checkinDates(checkins.map {it.date.toString()}, group, userPermissions)
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

fun FlowContent.checkinDates(dates: List<String>, group: Group, perms: UserPermissions) {
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
                a(classes = "btn b-none px-lg text-center") {+date}
            }
            for (i in 31 downTo  1) {
                val s = i.toString().padStart(2, '0')
                a(classes = "btn b-none px-lg text-center", href="/groups/${group.id}?date=${"2025-12-$s"}") {+"2025 / 12 / $s"}
            }
            for (i in 30 downTo  1) {
                val s = i.toString().padStart(2, '0')
                a(classes = "btn b-none px-lg text-center", href="/groups/${group.id}?date=${"2025-11-$s"}") {+"2025 / 11 / $s"}
            }
            for (i in 31 downTo  1) {
                val s = i.toString().padStart(2, '0')
                a(classes = "btn b-none px-lg text-center", href="/groups/${group.id}?date=${"2025-10-$s"}") {+"2025 / 10 / $s"}
            }
            for (i in 30 downTo  1) {
                val s = i.toString().padStart(2, '0')
                a(classes = "btn b-none px-lg text-center", href="/groups/${group.id}?date=${"2025-09-$s"}") {+"2025 / 09 / $s"}
            }
        }
    }
}

