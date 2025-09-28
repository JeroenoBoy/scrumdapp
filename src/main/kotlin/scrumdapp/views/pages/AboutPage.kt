package com.jeroenvdg.scrumdapp.views.pages

import kotlinx.html.FlowContent
import kotlinx.html.b
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

fun FlowContent.aboutPage() {
    h1 { +"Scrumdapp" }
    p { +"De ultieme check-in manager voor jouw project." }
    p {
        +"Scrumdapp, aka Scrum Daddy App is ontwikkeld uit frustratie om makkelijk check-ins bij te houden van je team. Wij willen hier een simpelere manier om dit bij te houden met een prachtige UI en security & privacy features by design. `(*>﹏<*)′ "
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
    h2 {+"Ontwikkeld door"}
    ul {
        li { +"Jeroen van de Geest" }
        li { +"Daan Meijneken" }
        li { +"Luc van Ogtrop" }
    }
}