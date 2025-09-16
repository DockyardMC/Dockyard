package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when player picks item from an entity")
data class PlayerPickItemFromEntityEvent(val player: Player, var entity: Entity, val includeData: Boolean, override val context: Event.Context): Event