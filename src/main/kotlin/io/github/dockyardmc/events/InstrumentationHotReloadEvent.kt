package io.github.dockyardmc.events

import kotlin.reflect.KClass

data class InstrumentationHotReloadEvent(val kclass: KClass<*>) : Event {
    override val context: Event.Context = Event.Context(isGlobalEvent = true)
}