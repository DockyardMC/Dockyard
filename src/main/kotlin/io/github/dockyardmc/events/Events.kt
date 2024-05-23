package io.github.dockyardmc.events

import io.github.dockyardmc.profiler.Profiler

object Events {
    private val profiler = Profiler()
    val eventMap = mutableMapOf<Class<out Event>, MutableList<ExecutableEvent<Event>>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event> on(noinline function: (event: T) -> Unit) {
        val eventType = T::class.java
        val eventList = eventMap.getOrPut(eventType) { mutableListOf() }
        eventList.add(ExecutableEvent(function as (Event) -> Unit))
    }

    fun dispatch(event: Event) {
        profiler.start("Events Dispatch", 5)
        val eventType = event.javaClass
        eventMap[eventType]?.forEach { executableEvent ->
            (executableEvent.function as (Event) -> Unit).invoke(event)
        }
        profiler.end()
    }
}

class ExecutableEvent<T : Event>(
    val function: (event: T) -> Unit
)