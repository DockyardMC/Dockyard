package io.github.dockyardmc.scheduler.runnables

import io.github.dockyardmc.DockyardServer
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun timedSequenceAsync(unit: (AsyncTimedSequence) -> Unit) {

    val runnable = DockyardServer.scheduler.runAsync {
        unit.invoke(AsyncTimedSequence())
    }
}


class AsyncTimedSequence() {

    fun wait(ticks: Duration) {
        Thread.sleep(ticks.inWholeMilliseconds)
    }

    fun wait(ticks: Int) {
        Thread.sleep((ticks * 50).toDuration(DurationUnit.MILLISECONDS).inWholeMilliseconds)
    }
}

val Int.ticks get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)

fun Int.ticks(): Duration = (this * 50).toDuration(DurationUnit.MILLISECONDS)