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

            }
            tbody {
                tr {
                    th(classes="row") { +"Op Tijd" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="blue") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="purple") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
                tr {
                    th(classes="row") { +"Te Laat" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="blue") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="purple") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
                tr {
                    th(classes="row") { +"Geoorloofd Afwezig" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="blue") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="purple") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
                tr {
                    th(classes="row") { +"Ongeoorloofd Afwezig" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="blue") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="purple") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
                tr {
                    th(classes="row") { +"Ziek" }
                    td(classes="red") { style="--size: 1"; span(classes="bg-hard"){ +"10" } }
                    td(classes="orange") { style="--size: 0.8"; span(classes="bg-hard"){ +"8" } }
                    td(classes="yellow") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                    td(classes="green") { style="--size: 0.7"; span(classes="bg-hard"){ +"7" } }
                    td(classes="blue") { style="--size: 0.2"; span(classes="bg-hard"){ +"2" } }
                    td(classes="purple") { style="--size: 0.3"; span(classes="bg-hard"){ +"3" } }
                }
            }
        }
    }
}
