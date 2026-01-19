package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.db.Group
import com.jeroenvdg.scrumdapp.db.GroupUser
import com.jeroenvdg.scrumdapp.middleware.ComparePermissions
import com.jeroenvdg.scrumdapp.models.UserPermissions
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.services.TrendsData
import com.jeroenvdg.scrumdapp.views.components.card
import com.jeroenvdg.scrumdapp.views.components.dropdown
import com.jeroenvdg.scrumdapp.views.components.dropdownItem
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.*
import kotlin.math.max

fun FlowContent.groupTrendsContent(application: Application, group: Group, trends: TrendsData, view: String) {
    fun TR.chartWidget(amount: Int, name: String, color: String) {
        if (amount > 0) {
            td(classes=color) {
                style="--size: ${max((amount.toFloat() / max(trends.highest, 1)), 0f)}"
                span(classes="bg-hard px-sm text-ellipse no-wrap") { style="overflow: hidden"
                    b {
                        +"${amount}x"
                    }
                    +" $name"
                }
            }
        }
    }

    card {
        h2{ +"Trends" }
    }

    card {
        div(classes="horizontal justify-between align-center w-full") {
            h3 { +"Presentie overzicht" }
            div {
                +"Periode"
                dropdown(if (view == "all") "Alles" else "14 dagen") {
                    dropdownItem(href=application.href(GroupsRouter.Group.Trends(group.id, view="all")), selected=view=="all") {
                        +"Alles"
                    }
                    dropdownItem(href=application.href(GroupsRouter.Group.Trends(group.id, view="last")), selected=view!="all") {
                        +"14 dagen"
                    }
                }
            }
        }
        div(classes="horizontal g-md") {
            table(classes="charts-css flex-1 bar stacked show-labels data-spacing-10 datasets-spacing-1 big-label") {
                style="--labels-size: 8em"
                thead { }
                tbody {
                    for (trend in trends) {
                        tr {
                            th(classes="row no-wrap") {
                                val name = trend.userName.split(" ")
                                +name.first()
                                if (name.size > 1) { +" ${name.last().first()}" }
                            }
                            td { style="--size: 0"; +" " }
                            chartWidget(trend.sickCount, "Ziek", "blue-dim")
                            chartWidget(trend.absentCount, "O.A.", "red-dim")
                            chartWidget(trend.verifiedAbsentCount, "G.A.", "green-dim")
                            chartWidget(trend.lateCount, "T.L.", "yellow")
                            chartWidget(trend.onTimeCount, "O.T.", "green")
                        }
                    }
                }
            }
        }
    }

//    card {
//        h3 { +"In Detail" }
//
//        for (trend in trends) {
//            div(classes="horizontal px-md align-center") {
//                span(classes="name-field b-none") {
//                    +trend.userName
//                }
//                a(href=application.href(Group.Trends.User(trend.groupId, trend.userId)), classes="btn btn-blue") {
//                    +"Meer"
//                }
//            }
//        }
//    }
}

fun FlowContent.groupExportContent(application: Application, currentUser: GroupUser, users: List<GroupUser>) {

    fun TABLE.userRow(groupUser: GroupUser) {
        tr {
            td(classes="name-field") { +groupUser.user.name }
            td(classes="horizontal justify-start") {
                if (groupUser == currentUser || ComparePermissions(currentUser.permissions, UserPermissions.Coach)) {
                    a(href=application.href(GroupsRouter.Group.Export.User(groupUser.groupId, groupUser.user.id)), classes="btn") {
                        +"Export"
                    }
                } else {
                    span(classes="btn") { style="opacity: 0.5"; +"Export" }
                }
            }
        }
    }

    card {
        h3 { +"Export" }
        table(classes="checkin-table") {
            thead {
                tr {
                    th(classes="text-left name-field") { +"Gebruiker" }
                    th { +"Acties" }
                }
            }
            tbody {
                this@table.userRow(currentUser)
                for (user in users.filter { it != currentUser }) {
                    this@table.userRow(user)
                }
            }
        }
    }
}
