package io.github.dockyardmc.spark

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickMonitorEvent
import me.lucko.spark.common.tick.AbstractTickReporter

class SparkTickReporter: AbstractTickReporter() {

    lateinit var listener: EventListener<Event>

    override fun close() {
        if(::listener.isInitialized) Events.unregister(listener)
    }

    override fun start() {
        listener = Events.on<ServerTickMonitorEvent> { event ->
            onTick(event.tickTime.toDouble())
        }
    }
}