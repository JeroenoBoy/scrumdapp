package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.services.TrendData
import com.jeroenvdg.scrumdapp.services.TrendsData
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.div
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
import kotlin.math.max
import kotlin.math.min

val colors = listOf("red", "green", "yellow", "blue", "orange", "aqua", "purple", "gray")

fun FlowContent.groupTrendsContent(trends: TrendsData) {
    card {
        h2{ +"Trends" }
    }
    card { id="trends-chart-2"
        h3 {+"Presentie overzicht"}
        div(classes="horizontal g-md") {
            ul(classes="charts-css legend rounded flex-0") {
                for (i in 0 until trends.size) {
                    li(classes=colors[i % colors.size]) {
                        span(classes="fg no-wrap") {
                            val name = trends[i].userName.split(" ")
                            +name[0]
                            if (name.size > 1) {
                                +" "
                                +name.last().first().toString()
                            }
                        }
                    }
                }
            }
            table(classes="charts-css flex-1 column multiple show-labels data-outside data-spacing-10 datasets-spacing-1 big-label") {
                thead {

                }
                tbody {
                    tr {
                        th(classes="row") { +"Op Tijd" }
                        for (i in 0 until trends.size) {
                            val trend = trends[i]
                            td(classes=colors[i % colors.size]) {
                                style="--size: ${max((trend.onTimeCount / max(trends.highest, 1)).toFloat(), 0.01f)}"
                                span(classes="bg-hard") {
                                    +trend.onTimeCount.toString()
                                }
                            }
                        }
                    }
                    tr {
                        th(classes="row") { +"Te Laat" }
                        for (i in 0 until trends.size) {
                            val trend = trends[i]
                            td(classes=colors[i % colors.size]) {
                                style="--size: ${max((trend.lateCount.toFloat() / max(trends.highest, 1).toFloat()), 0.01f)}"
                                span(classes="bg-hard") {
                                    +trend.lateCount.toString()
                                }
                            }
                        }
                    }
                    tr {
                        th(classes="row") { +"Geoorloofd Afwezig" }
                        for (i in 0 until trends.size) {
                            val trend = trends[i]
                            td(classes=colors[i % colors.size]) {
                                style="--size: ${max((trend.verifiedAbsentCount.toFloat() / max(trends.highest, 1).toFloat()), 0.01f)}"
                                span(classes="bg-hard") {
                                    +trend.verifiedAbsentCount.toString()
                                }
                            }
                        }
                    }
                    tr {
                        th(classes="row") { +"Ongeoorloofd Afwezig" }
                        for (i in 0 until trends.size) {
                            val trend = trends[i]
                            td(classes=colors[i % colors.size]) {
                                style="--size: ${max((trend.absentCount.toFloat() / max(trends.highest, 1).toFloat()), 0.01f)}"
                                span(classes="bg-hard") {
                                    +trend.absentCount.toString()
                                }
                            }
                        }
                    }
                    tr {
                        th(classes="row") { +"Ziek" }
                        for (i in 0 until trends.size) {
                            val trend = trends[i]
                            td(classes=colors[i % colors.size]) {
                                style="--size: ${max((trend.sickCount.toFloat() / max(trends.highest, 1).toFloat()), 0.01f)}"
                                span(classes="bg-hard") {
                                    +trend.sickCount.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
