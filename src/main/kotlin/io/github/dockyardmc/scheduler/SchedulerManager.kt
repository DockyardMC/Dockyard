package io.github.dockyardmc.scheduler

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent
import java.lang.IllegalStateException

object SchedulerManager {

    private val innerList: MutableList<Scheduler> = mutableListOf()
    val list get() = innerList.toList()

    fun register(scheduler: Scheduler) {
        if(innerList.contains(scheduler)) throw IllegalStateException("That schedules is already registered")
        innerList.add(scheduler)
    }

    fun unregister(scheduler: Scheduler) {
        innerList.remove(scheduler)
    }

    init {
        Events.on<ServerTickEvent> { event ->
            innerList.forEach { it.tickStart(event.serverTicks) }
        }
    }
}