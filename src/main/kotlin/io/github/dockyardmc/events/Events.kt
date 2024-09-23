package io.github.dockyardmc.events

import io.github.dockyardmc.profiler.Profiler
import kotlin.reflect.KClass

object Events {
    private val profiler = Profiler()
    val eventMap = mutableMapOf<KClass<out Event>, MutableList<EventListener<Event>>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event> on(noinline function: (event: T) -> Unit): EventListener<Event> {
        val eventType = T::class
        val eventList = eventMap.getOrPut(eventType) { mutableListOf() }
        val eventListener = EventListener(T::class, function as (Event) -> Unit)
        eventList.add(eventListener)
        return eventListener
    }

    fun unregister(event: EventListener<Event>) {
        val events = eventMap[event.type] ?: return
        events.remove(event)
    }

    fun dispatch(event: Event) {
        profiler.start("Events Dispatch", 5)
        val eventType = event::class
        eventMap[eventType]?.let { eventList ->
            val eventListCopy = eventList.toList()
            eventListCopy.forEach { executableEvent ->
                executableEvent.function.invoke(event)
            }
        }
        profiler.end()
    }
}

class EventListener<T : Event>(
    val type: KClass<out Event>,
    val function: (event: T) -> Unit,
)