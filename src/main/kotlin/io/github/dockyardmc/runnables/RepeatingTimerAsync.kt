package io.github.dockyardmc.runnables

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class RepeatingTimerAsync(var milliseconds: Long, var action: () -> Unit) {
    private val timer = Timer()

    fun run() {
        timer.scheduleAtFixedRate(0L, milliseconds) {
            try {
                action.invoke()
            } catch (ex: Exception) {
                log("Error in timer: ${ex.message}", LogType.ERROR)
            }
        }
    }
}