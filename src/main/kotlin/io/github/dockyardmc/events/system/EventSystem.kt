package io.github.dockyardmc.events.system

import io.github.dockyardmc.events.*
import io.github.dockyardmc.events.EventListener
import io.github.dockyardmc.utils.Disposable
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.util.*
import kotlin.reflect.KClass

abstract class EventSystem : Disposable {
    val eventMap = mutableMapOf<KClass<out Event>, HandlerList>()
    var filter = EventFilter.empty()

    val children = ObjectOpenHashSet<EventSystem>()
    open var parent: EventSystem? = null
        internal set // assigning to this value will fuck everything, there is no point having it public.
    open var name: String = "eventsystem-${UUID.randomUUID().toString().substring(0..7)}"

    /**
     * Registers a new event listener to this EventSystem
     *
     * @param function The function to be run when this event is called
     * @return The newly registered event listener, can be used when unregistering
     */
    inline fun <reified T : Event> on(noinline function: EventListenerFunction<T>): EventListener<Event> {
        val eventType = T::class
        val eventListener = EventListener(T::class, function as (Event) -> Unit)
        synchronized(eventMap) {
            val eventList = eventMap.getOrPut(eventType) { HandlerList() }
            eventList.add(eventListener)
        }
        return eventListener
    }

    /**
     * @return A flat list of all EventListeners registered to this EventSystem
     */
    fun eventList() = eventMap.flatMap { it.value.list }

    /**
     * Dispatches a new event into this EventSystem, this will call
     * all event listeners registered to this system, and dispatch
     * into all child EventSystems
     *
     * @param event The event being called
     * @return true if the event was dispatched, false if it was
     * filtered
     */
    open fun dispatch(event: Event): Boolean {
        if (!event.context.isGlobalEvent && !filter.check(event)) return false

        val eventType = event::class

        // we create a copy of the children array before running any handlers to
        //  ensure that if any children are added in the following listeners,
        //  we don't call the newly registered events
        val children = children.clone()
        eventMap[eventType]?.let { handlers ->
            handlers.listeners.forEach { executableEvent ->
                executableEvent.function.invoke(event)
            }
        }
        children.forEach { it.dispatch(event) }
        return true
    }

    /**
     * Unregisters a listener
     * @param event The EventListener object (returned from `EventSystem.on { ... }`)
     */
    @Synchronized
    open fun unregister(event: EventListener<Event>) {
        val events = eventMap[event.type] ?: return
        events.remove(event)
        if (events.isEmpty()) eventMap.remove(event.type)
    }

    /**
     * Unregisters all listeners
     *
     * **This does not remove children, use `clearChildren()` to remove all children**
     * @see clearChildren
     */
    @Synchronized
    open fun unregisterAllListeners() {
        eventMap.clear()
    }

    /**
     * Adds a child to this object
     *
     * @param child The child EventSystem to add
     * @throws IllegalStateException if the child is already registered to another parent
     */
    open fun addChild(child: EventSystem) {
        if (child.parent != null && child.parent != this)
            throw IllegalStateException("$child is already registered to parent ${child.parent}.")

        synchronized(children) {
            children += child
            child.parent = this
        }
    }

    /**
     * Removes a child from this object
     * @param child The child EventSystem to remove
     */
    @Synchronized
    open fun removeChild(child: EventSystem) {
        children -= child
        child.parent = null
    }

    /**
     * Removes all child EventSystems from this object
     */
    @Synchronized
    open fun clearChildren() {
        this.children.forEach { it.parent = null }
        this.children.clear()
    }

    /**
     * Removes this EventSystem from its parent, add attaches
     * it directly to a new parent (by default the master Events object)
     *
     * This means that this EventSystem will no longer inherit filters,
     * direct dispatches, other behaviour from its current parents.
     *
     * @param parent The new parent to attach this object to (defaults to `Events`)
     */
    fun fork(parent: EventSystem = Events) {
        dispose()
        parent.addChild(this)
    }

    /**
     * Detaches this EventSystem from its current parent. This does
     * not remove its children, or unregister any of its listeners.
     * But will no longer receive any events
     */
    fun detachParent() {
        parent?.removeChild(this)
    }

    /**
     * Creates a new EventPool with this EventSystem as its parent!
     *
     * @param name The name of the new EventPool
     * @return The newly created EventPool
     */
    fun subPool(name: String? = null): EventPool {
        return EventPool(parent = this, name = name)
    }

    override fun dispose() = detachParent()

    /**
     * @return A text representation of the tree below
     * this EventSystem, used for debugging
     */
    fun debugTree(indent: Int = 1): String {
        return buildString {
            append(name)
            append(" <gray>(${eventList().size} listeners)")
            children.forEach { child ->
                appendLine()
                append(" ".repeat(indent))
                append("<gray>⇒ <aqua>")
                append(child.debugTree(indent + 1))
            }
        }
    }

    override fun toString(): String {
        return "EventSystem($name)"
    }
}