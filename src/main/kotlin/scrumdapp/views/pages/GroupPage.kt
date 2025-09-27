package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.models.UserPermissions
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.table
import kotlinx.html.thead
import kotlinx.html.tbody
import kotlinx.html.tr
import kotlinx.html.th
import kotlinx.html.td
import kotlinx.html.span
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.input


fun FlowContent.groupPage(checkins: List<Checkin>, group: Group, perm: UserPermissions) {
    h1 {+group.name}
    div(classes = "horizontal g-lg") {
        div(classes= "vertical g-lg") {
            div(classes= "card vertical g-md") {
                i(classes= "px-lg-text-center") {+"pagina's"}
                hr {}
                a(href= "/groups/${group.id}/trends", classes= "btn b-none px-lg text-center") {+"Trends"}
                when {
                    perm.id <= UserPermissions.ScrumDad.id -> {
                        a(href= "/groups/${group.id}/users", classes= "btn b-none px-lg text-center") {+"Gebruikers"}
                        a(href= "/groups/${group.id}/config", classes= "btn b-none px-lg text-center") {+"Instellingen"}
                    }
                    perm.id <= UserPermissions.UserManagement.id -> {
                        a(href= "/groups/${group.id}/users", classes= "btn b-none px-lg text-center") {+"Gebruikers"}
                    }
                }
            }
            checkinDates(checkins.map {it.date.toString()}, perm)
        }
        checkinWidget(checkins)
    }
}

fun FlowContent.checkinDates(dates: List<String>, perm: UserPermissions) {
    div(classes = "card vertical g-md") {
        if (perm.id <= UserPermissions.CheckinManagement.id) {
            form(classes = "horizontal justify-between items-center") {
                i(classes = "px-lg my-auto") {+"Data" }
                input(classes = "btn btn-red", type = InputType.submit) {value="+"}
            }
        }
        hr {}
        dates.forEach { date ->
            a(classes = "btn b-none px-lg text-center") {+date}
        }
    }
}

fun FlowContent.checkinWidget(checkins: List<Checkin>) {
    div(classes = "card flex-1") {
        table(classes = "checkin-table") {
            thead {
                tr {
                    th(classes = "text-left name-field") {+"Naam"}
                    th(classes = "text-left pl-md") {+"Presentie"}
                    th {+"Check-in"}
                    th {+"Check-up"}
                    th(classes = "text-right") {+"Opmerkingen"}
                }
            }
            tbody {
                checkins.forEach { checkin ->
                    tr {
                        td(classes = "text-ellpise name-field") { +checkin.name }
                        td(classes = "pl-md") {} // Kleurtje moet nog
                        td(classes = "text-center") {}
                        td(classes = "text-center") {}
                        td(classes = "horizontal justify-between") {
                            span(classes = "text-ellpise") {
                                a(classes="btn btn-blue") {
                                    i(classes="material-icons") { +"view" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

fun FlowContent.editableCheckinWidget(checkins: List<Checkin>) {

}

