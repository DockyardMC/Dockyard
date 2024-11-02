package io.github.dockyardmc.events

import io.github.dockyardmc.utils.Disposable
import java.util.UUID
import kotlin.reflect.KClass

abstract class EventSystem : Disposable {
    val eventMap = mutableMapOf<KClass<out Event>, MutableList<EventListener<Event>>>()
    var filter: ((Event) -> Boolean) = { true }

    val children = mutableListOf<EventSystem>()
    open var parent: EventSystem? = null
    open var name: String = "eventsystem-${UUID.randomUUID().toString().substring(0..7)}"

    inline fun <reified T : Event> on(noinline function: EventListenerFunction<T>): EventListener<Event> {
        val eventType = T::class
        val eventList = eventMap.getOrPut(eventType) { mutableListOf() }
        val eventListener = EventListener(T::class, function as (Event) -> Unit)
        eventList.add(eventListener)
        return eventListener
    }

    fun eventList() = eventMap.flatMap { it.value }

    open fun dispatch(event: Event) {
        if (!filter(event)) return

        val eventType = event::class
        eventMap[eventType]?.let { eventList ->
            val eventListCopy = eventList.toList()
            eventListCopy.forEach { executableEvent ->
                executableEvent.function.invoke(event)
            }
        }
        children.forEach { it.dispatch(event) }
    }

    open fun unregister(event: EventListener<Event>) {
        val events = eventMap[event.type] ?: return
        events.remove(event)
        if (events.isEmpty()) eventMap.remove(event.type)
    }

    open fun addChild(child: EventSystem) {
        children += child
        child.parent = this
    }
    open fun removeChild(child: EventSystem) {
        children -= child
        child.parent = null
    }
    fun fork() {
        dispose()
        Events.addChild(this)
    }
    fun subPool(name: String? = null): EventPool {
        return EventPool(parent = this, name = name)
    }

    override fun dispose() {
        parent?.removeChild(this)
    }

    fun debugTree(indent: Int = 0): String {
        return buildString {
            append(name)
            children.forEach {
                appendLine()
                append(" ".repeat(indent+1))
                append("⇒ ")
                append(it.debugTree(indent + 1))
            }
        }
    }
}