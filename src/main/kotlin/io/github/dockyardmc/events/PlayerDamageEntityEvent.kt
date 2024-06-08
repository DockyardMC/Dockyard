package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player

@EventDocumentation("when player attacks another entity", true)
class PlayerDamageEntityEvent(var player: Player, var entity: Entity): CancellableEvent()