package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.middleware.user
import io.ktor.server.application.Application
import io.ktor.server.plugins.ratelimit.*

import io.ktor.server.application.install
import io.ktor.server.request.httpMethod
import kotlin.time.Duration.Companion.seconds

fun Application.configureRatelimitService() {
    install(RateLimit) {
        register(RateLimitName("postLimiter")) {
            rateLimiter(limit = 1, refillPeriod = 1.seconds )
            requestKey { applicationCall ->
                applicationCall.user.id
            }
            requestKey { requestType ->
                requestType.request.httpMethod.value
            }
            requestWeight { requestType, key ->
                when(key) {
                    "POST" -> 1
                    else -> 0
                }
            }
        }
    }
}