package com.jeroenvdg.scrumdapp.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale

@ConsistentCopyVisibility
data class YearWeek private constructor(val year: Int, val week: Int) {

    companion object {
        private val instances = hashMapOf<Int, YearWeek>();

        fun of(year: Int, week: Int): YearWeek {
            return instances.getOrPut(hashCode(year, week)) { YearWeek(year, week) }
        }

        fun of(date: LocalDate): YearWeek {
            return of(date.toJavaLocalDate())
        }

        fun of(date: java.time.LocalDate): YearWeek {
            val year = date.year
            val week = date.get(WeekFields.of(Locale.getDefault()).weekOfYear())
            return instances.getOrPut(hashCode(year, week)) { YearWeek(year, week) }
        }

        private fun hashCode(year: Int, week: Int): Int {
            return (year shl 6) + week
        }
    }

    fun toDate(): LocalDate {
        return LocalDate(year, 0, 0).plus(week, DateTimeUnit.WEEK)
    }

    fun ofDay(day: Int): LocalDate {
        if (day < 0) throw IllegalArgumentException("day must be greater than zero.")
        if (day > 7) throw IllegalArgumentException("day must be smaller than 7.")
        return toDate().plus(day, DateTimeUnit.DAY)
    }

    override fun hashCode(): Int {
        return hashCode(year, week)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YearWeek

        if (year != other.year) return false
        if (week != other.week) return false

        return true
    }
}