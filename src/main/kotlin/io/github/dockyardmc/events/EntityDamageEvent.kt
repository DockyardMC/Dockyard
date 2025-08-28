package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.registry.registries.DamageType

@EventDocumentation("when entity takes damage")
class EntityDamageEvent(val entity: Entity, var damage: Float, var damageType: DamageType, var attacker: Entity? = null, var projectile: Entity? = null, override val context: Event.Context) : CancellableEvent()