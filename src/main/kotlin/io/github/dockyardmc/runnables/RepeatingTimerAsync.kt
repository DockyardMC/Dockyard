package io.github.dockyardmc.runnables

import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class RepeatingTimerAsync(var milliseconds: Long, var action: () -> Unit) {
    private val timer = Timer()

    fun run() {
        timer.scheduleAtFixedRate(0L, milliseconds) {
            action.invoke()
        }
    }
}