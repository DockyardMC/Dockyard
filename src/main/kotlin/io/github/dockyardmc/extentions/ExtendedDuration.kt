package io.github.dockyardmc.extentions

import kotlin.time.Duration

fun Duration.toFormattedTime(format: String = "mm:ss"): String {
    val totalMilliseconds = this.inWholeMilliseconds
    val totalSeconds = totalMilliseconds / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60
    val remainingSeconds = totalSeconds % 60
    val remainingMinutes = totalMinutes % 60
    val remainingHours = totalHours % 24
    val remainingDays = totalHours / 24
    val remainingMillisecondsPart = totalMilliseconds % 1000

    return format.replace("yy", String.format("%02d", remainingDays / 365)) // approximate years
        .replace("dd", String.format("%02d", remainingDays % 365))
        .replace("hh", String.format("%02d", remainingHours))
        .replace("h", remainingHours.toString())
        .replace("mm", String.format("%02d", remainingMinutes))
        .replace("m", remainingMinutes.toString())
        .replace("ss", String.format("%02d", remainingSeconds))
        .replace("s", remainingSeconds.toString())
        .replace("SSS", String.format("%03d", remainingMillisecondsPart))
        .replace("SS", String.format("%02d", remainingMillisecondsPart / 10))
        .replace("S", (remainingMillisecondsPart / 100).toString())
        .replace("ms", totalMilliseconds.toString())
        .replace("s", totalSeconds.toString())
}