package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul

fun FlowContent.groupTrendsContent() {
    card {
        h2{ +"Trends" }
    }
    card { id="trends-chart-2"
        h3 {+"Presentie overzicht"}
        table(classes="charts-css column multiple show-labels data-outside data-spacing-10 datasets-spacing-1 big-label") {
            thead {
//                tr {
//                    th { +"Op Tijd" }
//                    th { +"Te Laat" }
//                    th { +"Geoorloofd afwezig" }
//                    th { +"Ongeoorloofd afwezig" }
//                    th { +"Ziek" }
//                }
            }
            tbody {
                tr {
                    th(classes="row") { +"Op Tijd" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="aqua") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="blue") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
                tr {
                    th(classes="row") { +"Te Laat" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.4"; +"3" }
                    td { style="--size: 0.8"; +"8" }
                    td { style="--size: 0.2"; +"8" }
                    td { style="--size: 0.01"; +"8" }
                }
                tr {
                    th(classes="row") { +"Geoorloofd Afwezig" }
                    td { style="--size: 0.5"; +"2" }
                    td { style="--size: 0.3"; +"3" }
                    td { style="--size: 1"; +"8" }
                    td { style="--size: 0.9"; +"8" }
                    td { style="--size: 0.1"; +"8" }
                }
                tr {
                    th(classes="row") { +"Ongeoorloofd Afwezig" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.01"; +"8" }
                    td { style="--size: 1.0"; +"8" }
                    td { style="--size: 0.8"; +"8" }
                    td { style="--size: 0.01"; +"0" }
                }
                tr {
                    th(classes="row") { +"Ziek" }
                    td { style="--size: 0.01"; +"8" }
                    td { style="--size: 0.01"; +"8" }
                    td { style="--size: 0.1"; +"8" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.01"; +"0" }
                }
            }
        }
        ul(classes="charts-css legend legend-inline b-gray rounded") {
            li { +"Jeroen" }
            li { +"Bob" }
            li { +"Daan" }
            li { +"Charlie" }
            li { +"Marley" }
            li { +"Diva" }
        }
    }

    card { id="trends-chart-1"
        h3 {+"Presentie Jeroen"}
        table(classes="charts-css column multiple show-labels data-outside data-spacing-10 big-label") {
            thead {
//                tr {
//                    th { +"Op Tijd" }
//                    th { +"Te Laat" }
//                    th { +"Geoorloofd afwezig" }
//                    th { +"Ongeoorloofd afwezig" }
//                    th { +"Ziek" }
//                }
            }
            tbody {
                tr {
                    th(classes="row") { +"Op Tijd" }
                    td { style="--size: 0.8"; +"8" }
                    td { style="--size: 0.8"; +"10 (80%)" }
                }
                tr {
                    th(classes="row") { +"Te Laat" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.4"; +"3 (10%)" }
                }
                tr {
                    th(classes="row") { +"Geoorloofd Afwezig" }
                    td { style="--size: 0.2"; +"2" }
                    td { style="--size: 0.8"; +"3 (10%)" }
                }
                tr {
                    th(classes="row") { +"Ongeoorloofd Afwezig" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.01"; +"0 (0%)" }
                }
                tr {
                    th(classes="row") { +"Ziek" }
                    td { style="--size: 0.01"; +"0" }
                    td { style="--size: 0.01"; +"0 (0%)" }
                }
            }
//            ul(classes="charts-css legend") {
//
//            }
        }
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