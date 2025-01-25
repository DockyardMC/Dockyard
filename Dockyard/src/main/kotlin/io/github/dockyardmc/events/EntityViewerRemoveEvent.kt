package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when viewer is removed from entity viewer list", true)
class EntityViewerRemoveEvent(var entity: Entity, var viewer: Player): CancellableEvent() {
    override val context = Event.Context(players = setOf(viewer), entities = setOf(entity))
}