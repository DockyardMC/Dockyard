package io.github.dockyardmc.events

import io.github.dockyardmc.events.system.EventSystem
import kotlin.reflect.KClass

object Events : EventSystem() {
    override var name: String = "GlobalEvents" // it would be really funny if API user renames this

    override fun dispatch(event: Event): Boolean {
        val success = super.dispatch(event)
        return success
    }

    /**
     * Removes all event listeners, and all children from main
     * events tree
     */
    override fun dispose() {
        this.children.clear()
        this.eventMap.clear()
    }

    override fun toString(): String {
        return "GlobalEvents"
    }
}

typealias EventListenerFunction<T> = (event: T) -> Unit
class EventListener<T : Event>(
    val type: KClass<out Event>,
    val function: EventListenerFunction<T>,
)