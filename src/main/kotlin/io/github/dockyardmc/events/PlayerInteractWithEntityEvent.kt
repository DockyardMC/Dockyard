package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand

@EventDocumentation("when player interacts with an entity", true)
class PlayerInteractWithEntityEvent(var player: Player, var entity: Entity, var interactionHand: PlayerHand): CancellableEvent() {
    override val context = elements(player, entity)
}