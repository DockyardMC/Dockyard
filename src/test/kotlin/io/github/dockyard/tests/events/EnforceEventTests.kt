package io.github.dockyard.tests.events

import io.github.dockyard.tests.EnforceTestsAbstract
import io.github.dockyardmc.events.Event

class EnforceEventTests : EnforceTestsAbstract() {
    override val testsPackage: String
        get() = "io.github.dockyard.tests.events"
    override val prodPackage: String
        get() = "io.github.dockyardmc.events"
    override val superClass: Class<out Any>
        get() = Event::class.java
}


