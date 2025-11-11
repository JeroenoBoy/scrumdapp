package com.jeroenvdg.scrumdapp.views.pages.groups

import kotlinx.html.FlowContent
import kotlinx.html.iframe

fun FlowContent.contentFrame(src: String = "") {
    iframe(classes="b-none frame-scale") {
        this.src = src
    }
}