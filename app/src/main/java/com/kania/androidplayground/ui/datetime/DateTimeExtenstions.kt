package com.kania.androidplayground.ui.datetime

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun LocalDateTime.toStringAsPattern(pattern: String) : String {
    val dateTimeFormat = DateTimeFormatter.ofPattern(pattern)
    return dateTimeFormat.format(this)
}

fun getLocalDateTimeFromInstantString(instantString: String) : LocalDateTime? {
    return try {
        LocalDateTime.ofInstant(Instant.parse(instantString), ZoneId.systemDefault())
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

fun getInstantStringFromLocalDateTime(localDateTime: LocalDateTime) : String? {
    return try {
        localDateTime.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
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