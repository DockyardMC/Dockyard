package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when player attacks another entity", true)
class PlayerDamageEntityEvent(var player: Player, var entity: Entity): CancellableEvent() {
    override val context = Event.Context(players = setOf(player), entities = setOf(entity))
}