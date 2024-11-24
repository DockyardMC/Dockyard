package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity

@EventDocumentation("when entity dies", true)
class EntityDeathEvent(val entity: Entity): CancellableEvent() {
    override val context = Event.Context(entities = setOf(entity))
}