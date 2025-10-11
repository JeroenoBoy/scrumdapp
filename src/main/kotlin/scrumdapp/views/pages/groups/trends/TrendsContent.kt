package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun FlowContent.groupTrendsContent() {
    card {
        h2{ +"Trends" }
    }
    card {
        h3{ +"Presentie" }
        table(classes="checkin-table") {
            thead {
                tr {
                    th { +"Naam" }
                    th { +"Afgelopen 10 dagen" }
                    th { +"Totaal" }
                }
            }
            tbody {
                tr {
                    td(classes="text-ellipse br-gray") { +"Jeroen van de Geest" }
                    td(classes="pl-md text-center") { icon(iconName="check",classes="green") }
                    td(classes="text-center") { span(classes="yellow") { +"1x T.L." } }
                }
                tr {
                    td(classes="text-ellipse br-gray") { +"Ben" }
                    td(classes="pl-md text-center") {
                        span("px-sm yellow") {+"3x T.L."}
                        span("px-sm red") {+"4x O.A."}
                        span("px-sm blue") {+"3x Z."}
                    }
                    td(classes="text-center") {
                        span("px-sm green-dim") {+"14x G.A."}
                        span("px-sm yellow") {+"18x T.L."}
                        span("px-sm red") {+"10x O.A."}
                        span("px-sm blue") {+"10x Z."}
                    }
                }
                tr {
                    td(classes="text-ellipse br-gray") { +"Daan Meijneken" }
                    td(classes="text-center") {
                        span("px-sm green-dim") {+"3x G.A."}
                        span("px-sm yellow") {+"3x T.L."}
                        span("px-sm red") {+"1x O.A."}
                    }
                    td(classes="text-center") {
                        span("px-sm green-dim") {+"14x G.A."}
                        span("px-sm yellow") {+"18x T.L."}
                        span("px-sm red") {+"10x O.A."}
                    }
                }
            }
        }
    }
}