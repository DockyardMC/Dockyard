package io.github.dockyardmc.events

import io.github.dockyardmc.utils.Disposable

class EventPool: Disposable {
    val eventList = mutableListOf<EventListener<Event>>()

    inline fun <reified T : Event> on(noinline function: (event: T) -> Unit): EventListener<Event> {
        val listener = Events.on<T>(function)
        eventList.add(listener)
        return listener
    }

    fun unregister(event: EventListener<Event>) {
        eventList.remove(event)
        Events.unregister(event)
    }

    override fun dispose() {
        eventList.forEach(Events::unregister)
        eventList.clear()
    }
}