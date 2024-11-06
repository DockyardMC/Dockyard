package io.github.dockyardmc.events

import io.github.dockyardmc.entities.Entity
import io.github.dockyardmc.world.World

class EntitySpawnEvent(val entity: Entity, val world: World, override val context: Event.Context) : CancellableEvent() {

}