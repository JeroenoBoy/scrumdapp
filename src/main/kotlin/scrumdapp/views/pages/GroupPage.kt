package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.db.Checkin
import com.jeroenvdg.scrumdapp.db.Group
import kotlinx.html.FlowContent
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


fun FlowContent.groupPage(checkins: List<Checkin>, group: Group) {
    h1 {+group.name}
    div(classes = "horizontal g-lg") {
        div(classes= "vertical g-lg") {
            div(classes= "card vertical g-md") {
                i(classes= "px-lg-text-center") {+"pagina's"}
                hr {}
                // Figure out how the buttons are going to work. (pre-render or reload or other?)
            }
            checkinDates(checkins.map {it.date.toString()})
        }
        checkinWidget(checkins)
    }
}

fun FlowContent.checkinDates(dates: List<String>) {
    div(classes = "card vertical g-md") {
        form(classes = "horizontal justify-between items-center") {
            i(classes = "px-lg my-auto") {+"Data"

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

