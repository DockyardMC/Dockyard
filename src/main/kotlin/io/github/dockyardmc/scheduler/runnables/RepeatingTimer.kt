package io.github.dockyardmc.scheduler.runnables

import io.github.dockyardmc.utils.Disposable
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class RepeatingTimer(var milliseconds: Long, var action: () -> Unit): Disposable {
    private val timer = Timer()

    var currentMilliseconds: Long = 0
    var paused = false

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    fun start(startingTime: Long = 0) {
        currentMilliseconds = startingTime
        timer.scheduleAtFixedRate(0L, 1) {
            if(paused) return@scheduleAtFixedRate

            currentMilliseconds++
            if(currentMilliseconds % milliseconds != 0L) return@scheduleAtFixedRate

            action.invoke()
        }
    }

    override fun dispose() {
        timer.cancel()
        timer.purge()
    }
}