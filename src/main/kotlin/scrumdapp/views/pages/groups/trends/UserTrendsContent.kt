package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.WeeklyStarData
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul

fun FlowContent.userTrendsContent(application: Application, user: GroupUser, weeklyStars: List<WeeklyStarData>) {
    card {
        div(classes="horizontal align-center g-md") {
            a(application.href(GroupsRouter.Group.Trends(user.groupId)), classes="btn b-none") {
                icon("arrow_back")
            }
            h2 { +"Trends van ${user.user.name}" }
            div(classes="flex-1")
        }
    }

    card {
        h3 { +"Presentie" }
        table(classes="charts-css line multiple show-5-secondary-axes") {
            thead {
            }
            tbody {
                tr {
                    td(classes="green") {style="--start:0.2;--end:0.5"}
                    td(classes="yellow") {style="--start:0.3;--end:0.2"}
                }
                tr {
                    td(classes="green") {style="--start:0.5;--end:0.3"}
                    td(classes="yellow") {style="--start:0.2;--end:0.1"}
                }
            }
        }
    }

    card {
        id="checkin-chart"
        h3 { +"Check-ins" }

        table(classes="charts-css show-labels line multiple show-5-secondary-axes charts-color-stars-table") {
            thead {
                tr {
                    th { attributes["scope"] = "row"; +"5" }
                    th { attributes["scope"] = "row"; +"4" }
                    th { attributes["scope"] = "row"; +"3" }
                    th { attributes["scope"] = "row"; +"2" }
                    th { attributes["scope"] = "row"; +"1" }
                    th { attributes["scope"] = "row"; +"0" }
                }
            }
            tbody {
                var prevMin = 0
                var prevMax = 0
                var prevAvg = 0f
                for (iv in weeklyStars.withIndex()) {

                    val i = iv.index
                    val data = iv.value
                    val min = data.checkin.min ?: 0
                    val max = data.checkin.max ?: 0
                    val avg = data.checkin.avg

                    if (i != 0) {
                        tr {
                            th {
                                attributes["scope"] = "row"
                                style = "align-items: start"
                                +"W${data.date.week}"
                            }
                            td { style="--start:${prevMin / 10.1};--end:${min / 10.1};" }
                            td { style="--start:${prevAvg / 10.1};--end:${avg / 10.1};" }
                            td { style="--start:${prevMax / 10.1};--end:${max / 10.1};" }
                        }
                    }

                    prevMin = min
                    prevMax = max
                    prevAvg = avg
                }
            }
        }

        br()

        ul(classes="charts-css legend legend-inline charts-color-stars-table") {
            li { +"Laagste" }
            li { +"Gemiddeld" }
            li { +"Hoogste" }
        }
    }

    card {
        h3 { +"Opmerkingen" }
    }
}