package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity

@EventDocumentation("when entity dies")
data class EntityDeathEvent(val entity: Entity, override val context: Event.Context) : CancellableEvent()