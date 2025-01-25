package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.registry.registries.DamageType

@EventDocumentation("when entity takes damage", true)
class EntityDamageEvent(val entity: Entity, var damage: Float, var damageType: DamageType, var attacker: Entity? = null, var projectile: Entity? = null): CancellableEvent() {
    override val context = Event.Context(entities = setOfNotNull(entity, attacker, projectile), other = setOf(damageType))
}