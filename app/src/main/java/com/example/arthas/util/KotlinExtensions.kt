package com.example.arthas.util

import java.sql.Timestamp

@Suppress("DEPRECATION")
fun Timestamp.plusDays(days: Int): Timestamp {
    val month = this.month
    val year = this.year
    val date = this.date
    val hours = this.hours
    val minutes = this.minutes
    val seconds = this.seconds
    val nanos = this.nanos

    return Timestamp(year, month, date + days, hours, minutes, seconds, nanos)
}