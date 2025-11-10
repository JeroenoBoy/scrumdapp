package com.jeroenvdg.scrumdapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.YearMonth

fun LocalDate.Companion.now(): LocalDate {
    return java.time.LocalDate.now().toKotlinLocalDate()
}

fun LocalDateTime.Companion.now(): LocalDateTime {
    return java.time.LocalDateTime.now().toKotlinLocalDateTime()
}

fun LocalDate.scrumdappFormat(small: Boolean = false): String {
    val y = year.toString().padStart(4, '0')
    val m = monthNumber.toString().padStart(2, '0')
    val d = dayOfMonth.toString().padStart(2, '0')
    return if (small) "$y/$m/$d" else "$y / $m / $d"
}

fun LocalDate.scrumdappUrlFormat(): String {
    val y = year.toString().padStart(4, '0')
    val m = monthNumber.toString().padStart(2, '0')
    val d = dayOfMonth.toString().padStart(2, '0')
    return "$y-$m-$d"
}

fun parseMonth(month: String): Month {
    return when (month.lowercase()) {
       "jan" -> return Month.JANUARY
        "feb" -> return Month.FEBRUARY
        "mar" -> return Month.MARCH
        "apr" -> return Month.APRIL
        "may" -> return Month.MAY
        "jun" -> return Month.JUNE
        "jul" -> return Month.JULY
        "aug" -> return Month.AUGUST
        "sep" -> return Month.SEPTEMBER
        "oct" -> return Month.OCTOBER
        "nov" -> return Month.NOVEMBER
        "dec" -> return Month.DECEMBER
        "january" -> return Month.JANUARY
        "february" -> return Month.FEBRUARY
        "march" -> return Month.MARCH
        "april" -> return Month.APRIL
        "june" -> return Month.JUNE
        "july" -> return Month.JULY
        "august" -> return Month.AUGUST
        "september" -> return Month.SEPTEMBER
        "october" -> return Month.OCTOBER
        "november" -> return Month.NOVEMBER
        "december" -> return Month.DECEMBER
        else -> throw IllegalArgumentException("Invalid month: $month")
    }
}

fun YearMonth.scrumdappFormat(): String = "${month.scrumdappFormat()} $year"

fun java.time.Month.scrumdappFormat(): String {
    return when (this) {
        Month.JANUARY -> "Januari"
        Month.FEBRUARY -> "Februari"
        Month.MARCH -> "Maart"
        Month.APRIL -> "April"
        Month.MAY -> "Mei"
        Month.JUNE -> "Juni"
        Month.JULY -> "Juli"
        Month.AUGUST -> "Augustus"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "Oktober"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
    }
}