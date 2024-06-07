package io.github.dockyardmc.events

open class CancellableEvent: Event {
    open var cancelled: Boolean = false
}