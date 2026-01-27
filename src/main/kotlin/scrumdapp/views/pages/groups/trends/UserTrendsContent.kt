package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.icon
import io.ktor.resources.href
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.onChange
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.ul

fun FlowContent.userTrendsContent(application: Application, user: GroupUser) {
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
        h3 { +"Check-ins / Check-ups" }

        table(classes="charts-css show-labels line multiple show-5-secondary-axes charts-color-check-ins") {
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
                tr {
                    th { attributes["scope"] = "row"; +"W29" }
                    td {style="--start:0.2;--end:0.5"}
                    td {style="--start:0.3;--end:0.2"}
                }
                tr {
                    th { attributes["scope"] = "row"; +"W30" }
                    td {style="--start:0.5;--end:0.3"}
                    td {style="--start:0.2;--end:1"}
                }
            }
        }

        br()

        ul(classes="charts-css legend legend-inline charts-color-check-ins") {
            li { +"Check-ins" }
            li { +"Check-ups" }
        }
    }

    card {
        h3 { +"Opmerkingen" }
    }
}