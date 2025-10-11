package com.jeroenvdg.scrumdapp.services

import com.jeroenvdg.scrumdapp.views.PageData
import com.jeroenvdg.scrumdapp.views.mainLayout
import com.jeroenvdg.scrumdapp.views.pages.errorPage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ExceptionContent(
    val code: Int,
    val title: String?,
    val message: String,
    val stackTrace: String
)

open class AppException(
    open val code: Int,
    override val message: String,
    open val title: String?,
    open val log: Boolean = false // Change this to enum?
): RuntimeException(message)

class ValidationException(
    override val message: String,
    override val code: Int = 400,
    override val title: String? = "Validatiefout"
): AppException(code, message, title, log = false)

fun exceptionFromThrowable(throwable: Throwable): ExceptionContent {
    return when (throwable) {
        is AppException -> ExceptionContent(
            code = throwable.code,
            title = throwable.title,
            message = throwable.message,
            stackTrace = throwable.stackTraceToString()
        )
        else -> ExceptionContent(
            code = 500,
            title = "Onverwachte fout",
            message = throwable.message ?: "Onbekende fout",
            stackTrace = throwable.stackTraceToString()
        )

    }
}

fun Application.configureExceptionService() {
    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            val content = exceptionFromThrowable(throwable)

            call.respondHtml {
                mainLayout(PageData("Fout-${content.code}")) {
                    errorPage(content)
                }
            }
        }

        status(
            HttpStatusCode.NotFound,
            HttpStatusCode.InternalServerError,
            HttpStatusCode.BadRequest,
        ) {call, statusCode ->
            when(statusCode) {
                HttpStatusCode.NotFound -> {
                    call.respondHtml {
                        mainLayout(PageData("Error")) {
                            errorPage(ExceptionContent(404, "Pagina niet gevonden", "Dit is gewoon een kleine testje", "meh"))
                        }
                    }
                }
            }
        }
    }
}