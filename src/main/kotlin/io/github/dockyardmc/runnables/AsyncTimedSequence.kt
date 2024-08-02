package io.github.dockyardmc.runnables

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun timedSequenceAsync(unit: (AsyncTimedSequence) -> Unit) {

    val runnable = AsyncRunnable {
        unit.invoke(AsyncTimedSequence())
    }
    runnable.run()
}


class AsyncTimedSequence() {

    fun wait(ticks: Duration) {
        Thread.sleep(ticks.inWholeMilliseconds)
    }
}

val Int.ticks get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)

fun Int.ticks(): Duration = (this * 50).toDuration(DurationUnit.MILLISECONDS)