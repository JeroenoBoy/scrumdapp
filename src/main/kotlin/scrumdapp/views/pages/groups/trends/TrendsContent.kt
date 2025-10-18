package com.jeroenvdg.scrumdapp.views.pages.groups.trends

import com.jeroenvdg.scrumdapp.services.TrendsData
import com.jeroenvdg.scrumdapp.views.components.card
import kotlinx.html.*
import kotlin.math.max

fun FlowContent.groupTrendsContent(trends: TrendsData) {
    fun TR.chartWidget(amount: Int, name: String, color: String) {
        if (amount > 0) {
            td(classes=color) {
                style="--size: ${max((amount.toFloat() / max(trends.highest, 1)), 0f)}"
                span(classes="bg-hard pt-sm") {
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

    card { id="trends-chart-2"
        h3 {+"Presentie overzicht"}
        div(classes="horizontal g-md") {
            table(classes="charts-css flex-1 column stacked show-labels data-outside data-spacing-10 datasets-spacing-1 big-label") {
                thead { }
                tbody {
                    for (trend in trends) {
                        tr {
                            th(classes="row") { +trend.userName }
                            chartWidget(trend.sickCount, "Ziek", "blue-dim")
                            chartWidget(trend.absentCount, "Ongeoorloofd Afwezig", "red-dim")
                            chartWidget(trend.verifiedAbsentCount, "Geoorloofd Afwezig", "green-dim")
                            chartWidget(trend.lateCount, "Te Laat", "yellow")
                            chartWidget(trend.onTimeCount, "Op Tijd", "green")
                        }
                    }
                }
            }
        }
    }
}
