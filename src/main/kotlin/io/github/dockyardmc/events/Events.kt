package io.github.dockyardmc.events

import io.github.dockyardmc.profiler.Profiler
import kotlin.reflect.KClass

object Events : EventSystem() {
    override var name: String = "GlobalEvents"
    private val profiler = Profiler()

    override fun dispatch(event: Event) {
        profiler.start("Events Dispatch", 5)
        super.dispatch(event)
        profiler.end()
    }
}

typealias EventListenerFunction<T> = (event: T) -> Unit
class EventListener<T : Event>(
    val type: KClass<out Event>,
    val function: EventListenerFunction<T>,
)