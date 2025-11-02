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
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h3
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

fun FlowContent.userTrendsContent(application: Application, user: GroupUser, view: String) {
    card {
        div(classes="horizontal align-center g-md") {
            a(application.href(GroupsRouter.Group.Trends(user.groupId)), classes="btn b-none") {
                icon("arrow_back")
            }
            h2 { +"Trends van ${user.user.name}" }
            div(classes="flex-1")
            form(classes="horizontal g-md align-center") {
                +"Periode"
                select(classes="input") { name="view"; onChange="this.form.submit()"
                    option { value="all"; selected=view == "all"; +"Alles" }
                    option { value="1"; selected=view=="1"; +"14 dagen" }
                }
            }
        }
    }

    card {
        h3 { +"Presentie" }
        table(classes="charts-css line multiple") {
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
        h3 { +"Check-ins / Check-ups" }
        table(classes="charts-css show-labels line multiple show-5-secondary-axes") {
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
                    td(classes="yellow") {style="--start:0.2;--end:0.5"}
                    td(classes="aqua") {style="--start:0.3;--end:0.2"}
                }
                tr {
                    td(classes="yellow") {style="--start:0.5;--end:0.3"}
                    td(classes="aqua") {style="--start:0.2;--end:1"}
                }
            }
        }
    }
}