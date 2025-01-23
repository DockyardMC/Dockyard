package io.github.dockyardmc.server

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.events.ServerTickMonitorEvent
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.runnables.RepeatingTimer

class ServerTickManager {

    val profiler = Profiler()
    var serverTicks: Long = 0
    val interval: Long = 50

    var timer: RepeatingTimer = RepeatingTimer(interval) {
        try {
            profiler.start("Tick", 5)
            serverTicks++
            Events.dispatch(ServerTickEvent(serverTicks))
            val end = profiler.end()
            Events.dispatch(ServerTickMonitorEvent(end, serverTicks, Event.Context()))
        } catch (ex: Exception) {
            log("Exception was thrown in the tick timer thread:", LogType.EXCEPTION)
            log(ex)
        }
    }

    fun start() {
        timer.start()
    }
}