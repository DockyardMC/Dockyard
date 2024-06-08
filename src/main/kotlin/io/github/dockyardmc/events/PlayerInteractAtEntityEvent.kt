package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.utils.Vector3f

@EventDocumentation("when player interacts with entity. Provides XYZ of click point unlike normal PlayerInteractWithEntityEvent", true)
class PlayerInteractAtEntityEvent(var player: Player, var entity: Entity, var clickPoint: Vector3f, var interactionHand: PlayerHand): CancellableEvent() {

}