package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.span
import kotlinx.html.tabIndex

fun FlowContent.dropdown(text: String, body: FlowContent.() -> Unit = {}) {
    div(classes="dropdown") { tabIndex="0"
        div(classes="dropdown-view") {
            span(classes="dropdown-text") { +text }
            icon(iconName="arrow_drop_up", classes="dropdown-arrow")
        }
        div(classes="dropdown-content") {
            div(classes="vertical g-md") {
                body()
            }
        }
    }
}

fun FlowContent.dropdownItem(href: String, body: FlowContent.() -> Unit = {}) {
    a(href=href, classes="btn b-none no-wrap") {
        body()
    }
}