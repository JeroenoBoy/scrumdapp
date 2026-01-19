package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.views.components.icon
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.img
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

fun FlowContent.aboutPage() {
    div(classes="horizontal g-lg align-center") {
        h1 { +"Scrumdapp" }
        a(href="https://github.com/JeroenoBoy/scrumdapp", classes="btn-gh") {
            img(alt="github-logo", src="/static/icons/github-mark.svg")
        }
    }

    p { +"De ultieme check-in manager voor jouw project." }
    p {
        +""" 
            Scrumdapp, aka Scrum Daily App is ontwikkeld uit frustratie om makkelijk check-ins bij te houden van je team. Wij willen hier een simpelere manier om dit bij te houden met een prachtige
            UI en security & privacy features by design. `(*>﹏<*)′ 
        """
    }
    p {
        +"""
           Tegenwoordig zijn er zo veel websites die bundels van React, Vue, Angular, Svelte of anders nodig hebben om te functioneren. 
           Daarom hebben wij een leuke uitdaging bedacht voor deze website. Een website 
        """.trimIndent()
        b { +"Zonder Javascript" }
        +"""
            . We houden ervan om te haten op JS. Maar is het daadwerkelijk mogelijk om zonder ook een goed functionerende website te maken.
            Wij vonden van wel! En dus hier, het resultaat
        """.trimIndent()
    }

    div(classes="spacer-lg")

    h2 { +"Ontwikkeld door" }
    ul {
        li { a(href="https://www.jeroenvdg.com", classes="a") { target="_blank"; +"Jeroen van de Geest"} }
        li { a(href="https://daan.meijneken.nl/", classes="a") { target="_blank"; +"Daan Meijneken" } }
        li { { target="_blank"; +"Luc van Ogtrop"} }
    }

    div(classes="spacer-lg")

    h2 { +"Credits" }
    ul {
        li { +"Wallpapers: "; a(href="https://gruvbox-wallpapers.vercel.app", classes="a") { target="_blank"; +"gruvbox-wallpapers" } }
        li { +"Ktor: "; a(href="https://www.jetbrains.com/", classes="a") { target="_blank"; +"Jetbrains 🥰" } }
    }
}