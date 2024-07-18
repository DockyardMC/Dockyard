package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.registry.DamageType

@EventDocumentation("when entity takes damage", true)
class EntityDamageEvent(val entity: Entity, var damage: Float, var damageType: DamageType, var attacker: Entity? = null, var projectile: Entity? = null): CancellableEvent()