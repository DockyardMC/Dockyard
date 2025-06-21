package io.github.dockyardmc.scheduler

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerTickEvent

object SchedulerManager {

    private val innerList: MutableList<GlobalScheduler> = mutableListOf()
    val list get() = innerList.toList()

    fun registerGlobal(scheduler: GlobalScheduler) {
        require(!innerList.contains(scheduler)) { "That schedules is already registered" }

        innerList.add(scheduler)
    }

    fun unregisterGlobal(scheduler: GlobalScheduler) {
        innerList.remove(scheduler)
    }

    init {
        Events.on<ServerTickEvent> { event ->
            innerList.forEach { it.tick() }
        }
    }
}