package io.github.dockyardmc.server

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.runnables.RepeatingTimerAsync

class ServerTickManager {

    val profiler = Profiler()
    var serverTicks: Int = 0
    val interval: Long = 50

    var timer: RepeatingTimerAsync = RepeatingTimerAsync(interval) {
        profiler.start("Tick", 5)
        serverTicks++
        Events.dispatch(ServerTickEvent(serverTicks))
        profiler.end()
    }


    fun start() {
        timer.run()
    }
}