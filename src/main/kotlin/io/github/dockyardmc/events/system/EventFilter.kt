package io.github.dockyardmc.events.system

import io.github.dockyardmc.events.Event

fun interface EventFilter {
    /**
     * Checks an event against this filter
     * @return true if the Event satisfies the conditions
     * of this Filter, and should be dispatched
     */
    fun check(event: Event): Boolean

    companion object {
        /**
         * An empty EventFilter, always allows events through
         */
        fun empty() = EventFilter { true }
    }
}