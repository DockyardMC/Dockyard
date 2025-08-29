package io.github.dockyard.tests

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.events.CancellableEvent
import io.github.dockyardmc.events.Event
import org.reflections.Reflections
import kotlin.test.Test
import kotlin.test.fail

class EnforceEventDocumentations {

    companion object {
        const val EVENTS_PACKAGE: String = "io.github.dockyardmc.events"
        val IGNORED_CLASSES: List<Class<out Event>> = listOf(
            CancellableEvent::class.java
        )
    }

    @Test
    fun test() {
        val reflections = Reflections(EVENTS_PACKAGE)
        val eventClasses = reflections.getSubTypesOf(Event::class.java).filterNot { IGNORED_CLASSES.contains(it) }
        val failed = mutableListOf<Class<out Event>>()
        eventClasses.forEach { kclass ->
            val annotations = kclass.annotations.filterIsInstance<EventDocumentation>()
            if (annotations.isEmpty()) {
                log("Event doesn't have documentation: ${kclass.simpleName}", LogType.ERROR)
                failed.add(kclass)
            }
        }
        if (failed.isNotEmpty()) {
            fail("The following events do not have documentation: ${failed.map { it.simpleName }}")
        }
    }
}