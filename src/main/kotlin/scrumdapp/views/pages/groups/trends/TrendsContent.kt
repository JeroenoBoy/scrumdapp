package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter
import com.jeroenvdg.scrumdapp.routes.groups.GroupsRouter.Group
import com.jeroenvdg.scrumdapp.services.TrendsData
import com.jeroenvdg.scrumdapp.views.components.card
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.*
import kotlin.math.max

fun FlowContent.groupTrendsContent(application: Application, trends: TrendsData, view: String) {
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

    card {
        h3 { +"In Detail" }

        for (trend in trends) {
            div(classes="horizontal px-md align-center") {
                span(classes="name-field b-none") {
                    +trend.userName
                }
                a(href=application.href(Group.Trends.User(trend.groupId, trend.userId)), classes="btn btn-blue") {
                    +"Meer"
                }
            }
        }
    }
}
