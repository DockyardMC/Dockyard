package io.github.dockyardmc.events

class EventPool(override var parent: EventSystem? = Events, name: String? = null) : EventSystem() {
    override var name: String = name ?: super.name.replace("eventsystem", "eventpool")

    init {
        parent?.addChild(this)
    }

    companion object {
        fun withFilter(name: String? = null, filter: (Event) -> Boolean) = EventPool(name = name)
            .also { it.filter = filter }
    }
}