package com.jeroenvdg.scrumdapp.routes.groups

import com.jeroenvdg.scrumdapp.db.GroupService
import com.jeroenvdg.scrumdapp.middleware.IsLoggedIn
import com.jeroenvdg.scrumdapp.services.EncryptionService
import com.jeroenvdg.scrumdapp.services.EncryptionServiceImpl
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

suspend fun Application.configureInviteRoutes() {
    val groupService = dependencies.resolve<GroupService>()
    val encryptionService = dependencies.resolve<EncryptionService>()

    val tokenLength = 60
    val tokenRegex = Regex("^[A-Za-z0-9]{$tokenLength}")

    routing {
        route("/invitations") {
            install(IsLoggedIn)

            get {
                val test = encryptionService.encryptString("HALLO")
                val decrypted = encryptionService.decryptString(test)
                val hashed = encryptionService.hashValue(decrypted)
                val testHash = encryptionService.compareHash("HALLO!", hashed)
                call.respond(HttpStatusCode.OK, "Encryped message: $test, decrypted: $decrypted, hash: $hashed, testHash: $testHash")

            }

            post {
                val token = call.queryParameters["token"]
                val password = call.receiveParameters()["group_password"]

                if (token.isNullOrBlank()) {
                    // return error model
                }


            }
        }
    }
}