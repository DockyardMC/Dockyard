package io.github.dockyardmc.runnables

import io.github.dockyardmc.periodic.Period
import io.github.dockyardmc.periodic.TickPeriod


class TickTimer {

    var ticksRemain = 0L
    var running = false
    lateinit var executor: () -> Unit

    fun runLater(ticks: Long, func: () -> Unit) {
        ticksRemain = ticks
        executor = func
        running = true
    }

    init {
        Period.on<TickPeriod> {
            if(!running) return@on
            if(ticksRemain == 0L) {
                executor.invoke()
                running = false
            } else ticksRemain--
        }
    }
}