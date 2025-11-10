package com.jeroenvdg.scrumdapp.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate

fun LocalDate.Companion.now(): LocalDate {
    return java.time.LocalDate.now().toKotlinLocalDate()
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
