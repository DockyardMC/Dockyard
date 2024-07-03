package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.DamageType

@EventDocumentation("when player is damaged", true)
class PlayerDamageEvent(val player: Player, val damageType: DamageType, val attacker: Entity? = null, val projectile: Entity? = null): CancellableEvent()