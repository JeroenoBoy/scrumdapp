package com.jeroenvdg.scrumdapp.views.pages

import com.jeroenvdg.scrumdapp.routes.HomeRouter
import com.jeroenvdg.scrumdapp.routes.UserSettingsRouter
import com.jeroenvdg.scrumdapp.utils.href
import com.jeroenvdg.scrumdapp.views.components.icon
import io.ktor.resources.href
import io.ktor.server.application.Application
import io.ktor.server.resources.href
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.ul

fun FlowContent.privacyPage(application: Application) {
    div (classes="c-x mt-b") {
        h1(classes="text-center") { +"Service Level Agreement"}
        div(classes="card g-md max-w-xl") {
            p {+"""
                Laatste datum van aanpassing: 2026/01/28
                Status Scrumdapp: Beta versie / "as-is". Er wordt gewerkt aan een nieuwere versie; deze huidige versie wordt enkel beperkt ondersteunt en onderhouden. 
                """.trimIndent()}
            div(classes="spacer-lg")
            h2 { +"1) Doel en scope SLA"}
            p { +"Dit document beschrijft wat jij, de eindgebruiker op dit moment mag verwachten van Scrumdapp. Dit document is van toepassing op enkel deze beta versie van Scrumdapp en wordt bijgewerkt en/of aangepast bij nieuwere versies." }
            div(classes="spacer-lg")
            p { +"De belangrijkste onderwerpen in deze SLA zijn:"}
            ul {
                li { +"Ondersteuning"}
                li { +"Beschikbaarheid"}
                li { +"Gegevensverwerking"}
                li { +"Contact & hulp bij problemen"}
            }

            div(classes="spacer-lg")
            h2 { +"2) Ondersteuning"}
            p { +"Aan de beta versie van Scrumdapp gaat niet meer actief worden gewerkt of doorontwikkeld. We houden de applicatie online tot een nader bericht, maar verwacht geen vaste uptimes of (feature-)updates."}

            div(classes="spacer-lg")
            h2 { +"3) Beschikbaarheid & onderhoud"}
            p {+"Op dit moment is het geen garantie dat de applicatie actief onderhoud in de vorm van updates & fixes gaat ontvangen. Wel proberen we deze beta versie van de app online te houden totdat een de nieuwe applicatie voldoende klaar is om gepubliceerd te worden."}
            div(classes="spacer-lg")
            p {+"Dit betekent dat we ons best blijven doen deze versie van Scrumdapp online te houden tot in ieder geval de publicatie van de volledige versie. Wanneer deze tijd komt zullen we alle gebruikers tijdig informeren."}

            div(classes="spacer-lg")
            h2 { +"4) Ondersteuning bij problemen & contact"}
            p { +"""
        Alle bovenstaande informatie wordt opgeslagen op een door ons beheerde database. 
        We hebben een aantal stappen genomen om data verlies, diefstal en lekken te vermijden en zorgvuldig om te gaan met alle data.
    """.trimIndent()}

            div(classes="spacer-lg")
            h2 { +"Hoe kunt je je informatie verwijderen van Scrumdapp?"}
            p { +"Ondersteuning bij problemen met de beta versie van Scrumdapp is gelimiteerd en gericht op het oplossen van problemen zoals: dataverlies, (grote) beveiligingsrisico’s en langdurige storingen of downtime."}
            div(classes="spacer-lg")
            p { +"We zullen dus ook niet snel aanpassingen doen aan de ui (user interface), nieuwe features toevoegen of kleine, niet kritieke bugs oplossen."}
            div(classes="spacer-lg")
            p {
                +"""
            Mocht je toch vragen hebben of een suggestie voor bijvoorbeeld een feature hebben is de makkelijkste manier om ons te bereiken door een """.trimIndent()

                a {
                    href = "mailto:info@scrumdapp.com"
                    attributes["style"] = "color: var(--red); cursor: pointer;"
                    +"mail "
                }

                +"of door een issue aan te maken op onze offciële "
                a {
                    href = "https://github.com/JeroenoBoy/scrumdapp"
                    attributes["style"] = "color: var(--red); cursor: pointer;"
                    +"Github repository"
                }
                +"."
                +"We geven geen garanties op de reactietijd op email’s, issues of pull request, maar we proberen binnen een werkdag te reageren."
            }
            div(classes="spacer-lg")
            h2 { +"5) Gegevens"}
            p { +"Om ervoor te zorgen dat Scrumdapp werkt slaan wij de volgende gegevens op:"}
            div(classes="spacer-lg")

            p { +"Accounts"}
            ul {
                li { +"Voor- en achternaam vanuit de OpenICT Discord server"}
                li { +"Discord gebruikers id"}
                li { +"Link naar Discord profielfoto"}
                li { +"Sessietoken"}
            }
            p { +"Groepen"}
            ul {
                li { +"Alle notites en andere informatie die bij een check-in genoteerd staat"}
                li { +"Wachtwoord & auth token als er een groepsuitnodiging actief is"}
            }

            div(classes="spacer-lg")
            h2 { +"6) Privacy & beveiliging"}
            p { +"""
            Alle gegevens die wij gebruiken & opslaan worden uitsluitend gebruikt om Scrumdapp goed te laten functioneren. Om gegevens te beveiligen en misbruik te voorkomen nemen wij maatregelen, maar verwacht geen actief nieuwe beveiligingsupdate of andere beveiligingsmaatregelen.
            Wel nemen wij actie wanneer er grote problemen zijn of er kritieke problemen worden gevonden. 
            Voor jij als gebruiker, probeer te vermijden gevoelige informatie in bijvoorbeeld notities te gebruiken. Bij (poging tot) misbruik van de app of gegevens van anderen waartoe toegang is verleend mogen (en kunnen) wij actie tegen jouw account ondernemen. 
            """.trimIndent()
            }

            div(classes="spacer-lg")
            h2 { +"7) Databehoud & verwijdering"}
            p { +"We bewaren alle gegevens totdat deze beta versie van Scrumdapp offline gaat. Tegen die tijd nemen we contact op over eventuele regelingen van migratie van informatie naar een nieuwere versie van de app." }
            p { +"Aangezien deze versie van Scrumdapp niet meer ondersteund wordt is het "
                b { +"jouw "}
                +"""
                    verantwoordelijkheid om te zorgen dat gegevens bewaard blijven. Dit kan het makkelijkst worden gedaan door regelmatig een backup te maken van jouw aanwezigheid door je gegeven te exporteren bij de trends pagina van elke groep. 
                    Mocht je geen gebruik meer willen maken van Scrumdapp kun je je account verwijderen via de 
                """.trimIndent()
                a {
                    href = application.href(UserSettingsRouter())
                    attributes["style"] = "color: var(--red); cursor: pointer;"
                    +" instellingen pagina"
                }
                +". Groepen kunnen worden verwijderd door de beheerder (Lord of Scrum). Hierbij een herinnering dat we na het verwijderen van een account of groep geen gegevens meer terug kunnen halen en daarbij ook niet helpen."
            }

            div(classes="spacer-lg")
            h2 { +"8) Beperkingen & aansprakelijkheid"}
            p { +"""
            Het gebruik van Scrumdapp is geheel vrijwillig, vrijblijvend en op risico van de eindgebruiker. We zijn niet direct aansprakelijk voor schade veroorzaakt door zaken zoals downtime, onverwachts dataverlies, etc aangezien er aan deze betaversie niet meer actief wordt gewerkt en wordt aangeboden “as-is”. 
            Als je zelf aanpassingen of veranderingen wilt maken aan de applicatie verwijzen we je naar de licentie op onze
        """.trimIndent()
                a {
                    href = "https://github.com/JeroenoBoy/scrumdapp"
                    attributes["style"] = "color: var(--red); cursor: pointer;"
                    +" Github repository"
                }
                +"."
            }
        }
    }
    div(classes="horizontal c-xy p-sm") {
        a(classes="btn", href=application.href(HomeRouter())) {
            icon(iconName="undo", classes="green")
                +"Terug"
        }
    }
}