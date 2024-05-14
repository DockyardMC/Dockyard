package io.github.dockyardmc.runnables

import java.util.Timer
import kotlin.concurrent.schedule

class RepeatingTimerAsync(var milliseconds: Long, var action: () -> Unit) {
    private val timer = Timer()

    
    fun run() {
        timer.schedule(0L, milliseconds) {
            action.invoke()
        }
    }
}