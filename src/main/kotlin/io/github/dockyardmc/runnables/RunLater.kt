package io.github.dockyardmc.runnables

import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun runLater(delay: Int, unit: () -> Unit) {
    val task = AsyncRunnable {
        val duration = (delay * 0.05).toDuration(DurationUnit.SECONDS)
        Thread.sleep(duration.inWholeMilliseconds)
    }
    task.callback = {
        unit.invoke()
    }
    task.execute()
}