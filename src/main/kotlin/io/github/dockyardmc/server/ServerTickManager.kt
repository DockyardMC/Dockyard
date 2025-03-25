package io.github.dockyardmc.server

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.events.ServerTickMonitorEvent
import io.github.dockyardmc.scheduler.runnables.RepeatingTimer
import io.github.dockyardmc.utils.now

class ServerTickManager {

    var serverTicks: Long = 0
    val interval: Long = 50

    var timer: RepeatingTimer = RepeatingTimer(interval) {
        try {
            DockyardServer.scheduler.run {
                val start = now()

                serverTicks++
                Events.dispatch(ServerTickEvent(serverTicks))

                val end = now()
                Events.dispatch(ServerTickMonitorEvent(end - start, serverTicks, Event.Context()))
            }
        } catch (ex: Exception) {
            log("Exception was thrown in the tick timer thread:", LogType.EXCEPTION)
            log(ex)
        }
    }

    fun start() {
        timer.start()
    }
}