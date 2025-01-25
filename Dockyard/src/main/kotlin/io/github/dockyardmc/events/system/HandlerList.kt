package io.github.dockyardmc.events.system

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.EventListener

class HandlerList {
    internal val list = mutableListOf<EventListener<Event>>()
    var listeners = arrayOf<EventListener<Event>>()
        private set

    fun add(listener: EventListener<Event>) {
        list += listener
        bake()
    }

    fun remove(listener: EventListener<Event>) {
        list -= listener
        bake()
    }

    fun isEmpty() = list.isEmpty()

    fun bake() {
        listeners = list.toTypedArray()
    }

}