package io.github.dockyardmc.events

import io.github.dockyardmc.annotations.EventDocumentation
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.world.World

@EventDocumentation("when entity is spawned in a world")
data class EntitySpawnEvent(val entity: Entity, val world: World, override val context: Event.Context) : CancellableEvent()