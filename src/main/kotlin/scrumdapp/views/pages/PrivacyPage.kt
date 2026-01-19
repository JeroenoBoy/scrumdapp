package com.jeroenvdg.scrumdapp.views.pages

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.ul

fun FlowContent.privacyPage() {
    h1 { +"Privacy statement"}
    p {+"""
        Dit project is een beta versie van Scrumdapp. Gebruik en vul data aan op eigen risico!
        Voor problemen en/of vragen zie "Hoe kun je ons bereiken" sectie van deze pagina.
    """.trimIndent()}

    div(classes="spacer-lg")
    h2 { +"Welke informatie slaan wij op?"}
    p { +"Om ervoor te zorgen dat Scrumdapp goed werkt slaan wij de volgende gegevens op:" }

    div(classes="spacer-lg")
    p { +"Accounts:"}
    ul {
        li { + "Je gebruikersnaam in de OpenICT Discord server (voor + achternaam)"}
        li { + "Discord gebruikers id"}
        li { + "Link naar Discord profielfoto"}
    }

    p { +"Groepen (Squads)"}
    ul {
        li { +"Sterren en opmerkingen bij dagelijkse checkins" }
        li { +"Groepsnotities"}
        li { +"Naam van de groep en de permissies van elke gebruiker in desbetreffende groep"}
        li { +"Wachtwoord & auth token als er een uitnodiging voor een groep aanwezig is (deze verloopt automatisch na 24 uur)"}
    }

    div(classes="spacer-lg")
    h2 { +"Voor wie is deze informatie zichtbaar?"}
    p {+"""
        Wanneer jij (de gebruiker van Scrumdapp) een groep aanmaakt of bij een groep aansluit is je voor- en achternaam zichtbaar voor alle leden in de groep.
        Ook is alle informatie die is ingevuld bij een checkin zichtbaar voor alle leden van de desbetreffende groep. 
        Je Discord gebruikers id en profielfoto zijn niet zichtbaar voor gebruikers anders dan jezelf. 
    """.trimIndent()}

    div(classes="spacer-lg")
    h2 { +"Hoe bewaren wij informatie?"}
    p { +"""
        Alle bovenstaande informatie wordt opgeslagen op een door ons beheerde database. 
        We hebben een aantal stappen genomen om data verlies, diefstal en lekken te vermijden en zorgvuldig om te gaan met alle data.
    """.trimIndent()}

    div(classes="spacer-lg")
    h2 { +"Hoe kunt je je informatie verwijderen van Scrumdapp?"}
    p { +"""
        Data kan op twee manieren uit Scrumdapp worden verwijderd. 
        Als eerste worden alle checkins en notities verwijderd wanneer de eigenaar (Lord of Scrum) van een groep ervoor kiest deze te verwijdern.
        De tweede manier is om naar je persoonlijke instellingen te gaan (hover over je profielfoto rechtsboven op het hoofdscherm) en kies ervoor je account te verwijderen.
        Door dit te doen wordt alle informatie, inclusief checkins in alle groepen waar je onderdeel uitmaakte verwijderd.
    """.trimIndent()}

    div(classes="spacer-lg")
    h2 { +"Hoe kunt je ons bereiken?"}
    p { +"""Voor vragen, problemen of suggesties is de makkelijkste manier een discussion of issue aan te maken op onze """.trimIndent()
        a {
            href = "https://github.com/JeroenoBoy/scrumdapp"
            attributes["style"] = "cursor: pointer;"
            +"Github repository."
        }
    }
}