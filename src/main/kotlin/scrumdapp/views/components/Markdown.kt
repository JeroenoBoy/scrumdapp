package com.jeroenvdg.scrumdapp.views.components

import kotlinx.html.FlowContent
import kotlinx.html.b
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.h4
import kotlinx.html.h5
import kotlinx.html.hr
import kotlinx.html.i
import kotlinx.html.s
import kotlinx.html.span
import kotlinx.html.u

val lineRegex = Regex("""(?:\*{1,2})|(?:`{1,2})|(?:~{1,2})|(?:[\[\]()])|(?:__)|(?:[^`*_~\[\]()]+)""")

fun FlowContent.renderMarkdown(markdown: String) {
    div(classes="md-content") {
        for (line in markdown.split("\n")) {
            renderMarkdownLine(line)
        }
    }
}

fun FlowContent.renderMarkdownLine(line: String) {
    when {
        line.startsWith("---") -> hr(classes="md-line md-hr")
        line.startsWith("- ") -> div(classes="md-line md-bp") { renderMarkdownText(line.substring(2)) }
        line.startsWith("#") -> markdownRenderHeading(line)
        else -> div(classes="md-line") { renderMarkdownText(line) }
    }
}

fun FlowContent.markdownRenderHeading(line: String) {
    when {
        line.startsWith("# ") -> h2(classes="md-line md-h1") { renderMarkdownText(line.substring(2)) }
        line.startsWith("## ") -> h3(classes="md-line md-h2") { renderMarkdownText(line.substring(3)) }
        line.startsWith("### ") -> h4(classes="md-line md-h3") { renderMarkdownText(line.substring(4)) }
        line.startsWith("#### ") -> h5(classes="md-line md-h4") { renderMarkdownText(line.substring(5)) }
    }
}

fun FlowContent.renderMarkdownText(text: String) {
    val tokens = lineRegex.findAll(text).map { it.groupValues[0] }.toList()
    val ctx = TokenContext(tokens)
    span(classes="md-text") {
        while (ctx.hasNext()) {
            markdownRenderText(ctx, true, true, true)
        }
    }
}

fun FlowContent.markdownRenderText(ctx: TokenContext, textStyling: Boolean, block: Boolean, hyperlink: Boolean) {
    when (ctx.peek()) {
        "**" if textStyling -> makdownRenderBold(ctx, hyperlink)
        "*" if textStyling -> markdownRenderItalic(ctx, hyperlink)
        "__" if textStyling -> markdownRenderUnderline(ctx, hyperlink)
        "~~" if textStyling -> markdownRenderStrike(ctx, hyperlink)
        "`" if block -> markdownRenderBlock(ctx)
        else -> {
            +ctx.next()
        }
    }
}

fun FlowContent.makdownRenderBold(ctx: TokenContext, hyperlink: Boolean) {
    b(classes="md-text-bold") {
        renderUntil(ctx, "**", true, true, hyperlink)
    }
}

fun FlowContent.markdownRenderItalic(ctx: TokenContext, hyperlink: Boolean) {
    i(classes="md-text-italic") {
        renderUntil(ctx, "*", true, true, hyperlink)
    }
}

fun FlowContent.markdownRenderUnderline(ctx: TokenContext, hyperlink: Boolean) {
    u(classes="md-text-underline") {
        renderUntil(ctx, "__", true, true, hyperlink)
    }
}

fun FlowContent.markdownRenderStrike(ctx: TokenContext, hyperlink: Boolean) {
    s(classes="md-text-strike") {
        renderUntil(ctx, "~~", true, true, hyperlink)
    }
}

fun FlowContent.markdownRenderBlock(ctx: TokenContext) {
    span(classes="md-text-block") {
        renderUntil(ctx, "`", false, false, false)
    }
}

fun FlowContent.renderUntil(ctx: TokenContext, token: String, textStyling: Boolean, block: Boolean, hyperlink: Boolean) {
    ctx.incr()
    while (ctx.hasNext()) {
        if (ctx.peek() == token) {
            ctx.incr()
            break
        }
        markdownRenderText(ctx, textStyling, block, hyperlink)
    }
}

data class TokenContext(val tokens: List<String>, var current: Int = 0) {
    fun peek() = tokens[current]
    fun hasNext() = tokens.size > current
    fun incr() { current += 1 }
    fun next(): String {
        current += 1
        return tokens[current - 1]
    }
}