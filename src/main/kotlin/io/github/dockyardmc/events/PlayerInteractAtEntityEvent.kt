package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.maths.vectors.Vector3f

@EventDocumentation("when player interacts with entity. Provides XYZ of click point unlike normal `PlayerInteractWithEntityEvent`")
data class PlayerInteractAtEntityEvent(var player: Player, var entity: Entity, var clickPoint: Vector3f, var interactionHand: PlayerHand, override val context: Event.Context): CancellableEvent()