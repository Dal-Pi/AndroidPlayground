package com.kania.androidplayground.ui.datetime

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toStringAsPattern(pattern: String) : String {
    val dateTimeFormat = DateTimeFormatter.ofPattern(pattern)
    return dateTimeFormat.format(this)
}

fun getOffsetDateTimeFromUtcStringInSeconds(dateTimeStringInSeconds: String) : OffsetDateTime {
    val patternInSeconds = "yyyy-MM-dd'T'HH:mm:ssX"
    val dateTimeFormatter = DateTimeFormatter.ofPattern(patternInSeconds)

    return LocalDateTime.from(
        dateTimeFormatter.parse(dateTimeStringInSeconds)
    ).atOffset(ZoneOffset.UTC)
}

fun getZoneDateTimeAsSystemDefaultFromUtcStringInSeconds(dateTimeStringInSeconds: String) : ZonedDateTime {
    val offsetDateTime = getOffsetDateTimeFromUtcStringInSeconds(dateTimeStringInSeconds)
    return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault())
}

fun getLocalDateTimeFromUtcStringInSeconds(dateTimeStringInSeconds: String) : LocalDateTime {
    val zoneDateTime = getZoneDateTimeAsSystemDefaultFromUtcStringInSeconds(dateTimeStringInSeconds)
    return zoneDateTime.toLocalDateTime()
}