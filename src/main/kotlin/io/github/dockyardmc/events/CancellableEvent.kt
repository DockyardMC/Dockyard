package io.github.dockyardmc.events

abstract class CancellableEvent: Event {
    open var cancelled: Boolean = false
}