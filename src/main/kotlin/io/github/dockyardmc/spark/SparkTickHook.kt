package io.github.dockyardmc.spark

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import me.lucko.spark.common.tick.AbstractTickHook

class SparkTickHook : AbstractTickHook() {
    lateinit var listener: EventListener<Event>

    override fun start() {
        listener = Events.on<ServerTickEvent> {
            onTick()
        }
    }

    override fun close() {
        if (::listener.isInitialized) Events.unregister(listener)
    }

}