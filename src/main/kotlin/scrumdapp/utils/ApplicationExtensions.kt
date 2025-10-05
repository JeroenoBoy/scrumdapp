package com.jeroenvdg.scrumdapp.utils

import io.ktor.server.plugins.di.DependencyKey
import io.ktor.server.plugins.di.DependencyResolver
import io.ktor.server.plugins.di.getBlocking

inline fun <reified T>DependencyResolver.resolveBlocking(): T {
    return getBlocking<T>(DependencyKey<T>())
}