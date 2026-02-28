package com.doptsw.sle.core

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val displayDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA)

fun todayString(): String = LocalDate.now().toString()

fun formatDisplayDate(value: String): String {
    return runCatching { LocalDate.parse(value).format(displayDateFormatter) }.getOrDefault(value)
}

fun monthTitle(month: YearMonth): String {
    return "${month.year}년 ${month.monthValue}월"
}
