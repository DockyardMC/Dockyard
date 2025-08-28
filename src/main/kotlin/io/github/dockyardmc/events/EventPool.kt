package io.github.dockyardmc.events

import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.events.system.EventSystem

class EventPool(override var parent: EventSystem? = Events, name: String? = null) : EventSystem() {
    override var name: String = name ?: super.name.replace("event_system", "event_pool")

    init {
        parent?.addChild(this)
    }

    /**
     * Attaches a new filter to this EventPool
     *
     * @param filter The new filter to attach
     * @return this
     */
    fun withFilter(filter: EventFilter): EventPool {
        this.filter = filter
        return this
    }

    override fun toString(): String {
        return "EventPool($name)"
    }

    companion object {
        /**
         * Creates a new EventPool with the given name, and given filter
         * @param name The name
         * @param filter The filter
         */
        fun withFilter(name: String? = null, filter: EventFilter) = EventPool(name = name)
            .withFilter(filter)
    }
}