package com.jeroenvdg.scrumdapp.views

import com.jeroenvdg.scrumdapp.middleware.user
import com.jeroenvdg.scrumdapp.views.components.navbar
import io.ktor.server.routing.RoutingCall
import kotlinx.html.BODY
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.styleLink
import kotlinx.html.title

data class PageData(val title: String)
data class DashboardPageData(val title: String, val call: RoutingCall, val background: String? = "15")

fun HTML.mainLayout(pageData: PageData, builder: BODY.() -> Unit = {}) {
    head {
        title("${pageData.title} | Scrumdapp")
        link("/static/theme.css", rel="stylesheet")
        link("/static/styles.css", rel="stylesheet")
        link("https://fonts.googleapis.com", rel = "preconnect")
        link("https://fonts.gstatic.com", rel = "preconnect")
        styleLink("https://fonts.googleapis.com/css2?family=Libertinus+Mono&family=Libertinus+Serif:ital,wght@0,400;0,600;0,700;1,400;1,600;1,700&display=swap")
        styleLink("https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0&icon_names=add,arrow_back,bar_chart,cancel,check,delete_forever,edit,groups," +
                "logout,settings,texture,undo")
    }
    body {
        builder()
    }
}

fun HTML.dashboardLayout(pageData: DashboardPageData, builder: FlowContent.() -> Unit = {}) {
    mainLayout(PageData(pageData.title)) {
        div { id = "app"
            img(alt="bg-img", src="/static/backgrounds/${pageData.background ?: "15"}.webp", classes="bg-img")
            navbar(pageData.call.user)
            div(classes="nav-height")
            div(classes="spacer-xl")
            div(classes="container-parent") {
                div(classes="container") { id="dashboard-content"
                    builder()
                }
            }
        }
    }
}