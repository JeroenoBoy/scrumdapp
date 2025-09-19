package com.jeroenvdg

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureTemplating()
    configureFrameworks()
    configureDatabases()
    configureRouting()
}
