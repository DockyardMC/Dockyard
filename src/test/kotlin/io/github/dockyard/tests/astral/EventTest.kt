package io.github.dockyard.tests.astral

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events

class EventTest : DockyardTest() {

    override fun testSpecificSetup() {
    }

    override fun testSpecificCleanup() {
    }

    lateinit var event: RandomAssEvent

    override fun createTestSteps() {
        addStep("call event") {
            Events.dispatch(RandomAssEvent(Event.Context.EMPTY, true))
        }

        addWaitForEvent<RandomAssEvent> { event ->
            this.event = event
        }

        addAssert("gay is true") { event.gay }
    }
}