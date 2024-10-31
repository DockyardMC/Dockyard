package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.DamageType

@EventDocumentation("when player is damaged", true)
class PlayerDamageEvent(val player: Player, var damage: Float, var damageType: DamageType, var attacker: Entity? = null, var projectile: Entity? = null): CancellableEvent() {
    override val context = Event.Context(players = setOf(player), entities = setOfNotNull(attacker, projectile), other = setOf(damageType))
}