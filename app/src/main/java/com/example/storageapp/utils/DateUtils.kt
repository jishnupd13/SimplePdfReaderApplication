package com.example.storageapp.utils

import java.text.SimpleDateFormat
import java.util.*

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ENGLISH)
    return format.format(date)
}

fun currentTimeToLong(): Long {
    return System.currentTimeMillis()
}

fun convertDateToLong(date: String): Long {
    val df = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ENGLISH)
    return df.parse(date)?.time ?: 0L
}


 fun epochToIso8601(time: Long): String {
    val format = "dd MMM yyyy"
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(time * 1000))
}
