package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.DamageType

@EventDocumentation("when player is damaged", true)
class PlayerDamageEvent(val player: Player, var damage: Float, var damageType: DamageType, var attacker: Entity? = null, var projectile: Entity? = null): CancellableEvent()