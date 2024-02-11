package com.kania.androidplayground.ui.datetime

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.to_yyyyMMddTHHmmss_string() : String {
    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return dateTimeFormat.format(this)
}

fun LocalDateTime.to_yyyyMMddTHHmmssSSSSSS_string() : String {
    val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    return dateTimeFormat.parse(toString()).toString()
}