package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.FlowContent
import kotlinx.html.span

fun FlowContent.icon(iconName: String, classes: String? = null) {
    span(classes="icon material-symbols-outlined"+if (classes != null) " $classes" else "") { +iconName }
}