package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.middleware.user
import io.ktor.server.application.Application
import io.ktor.server.plugins.ratelimit.*

import io.ktor.server.application.install
import kotlin.time.Duration.Companion.seconds

fun Application.configureRatelimitService() {
    install(RateLimit) {
        register(RateLimitName("checkinSubmit")) {
            rateLimiter(limit = 1, refillPeriod = 1.seconds )
            requestKey { applicationCall ->
                applicationCall.user.id
            }
        }
    }
}

