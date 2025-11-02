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
                    option { value="1"; selected=view=="1"; +"Sprint 1" }
                    option { value="2"; selected=view=="2"; +"Sprint 2" }
                    option { value="3"; selected=view=="3"; +"Sprint 3" }
                    option { value="4"; selected=view=="4"; +"Sprint 4" }
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
                    td {style="--start:0.2;--end:0.5"}
                    td {style="--start:0.3;--end:0.2"}
                }
                tr {
                    td {style="--start:0.5;--end:0.3"}
                    td {style="--start:0.2;--end:0.1"}
                }
            }
        }
    }
}