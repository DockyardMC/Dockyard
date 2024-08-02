package io.github.dockyardmc.runnables

import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun runLaterAsync(ticks: Int, unit: () -> Unit) {
    val task = AsyncRunnable {
        val duration = (ticks * 0.05).toDuration(DurationUnit.SECONDS)
        Thread.sleep(duration.inWholeMilliseconds)
    }
    task.callback = {
        unit.invoke()
    }
    task.run()
}