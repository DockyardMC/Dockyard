package io.github.dockyardmc.entity.handlers

import io.github.dockyardmc.entity.Entity

class EntityMovementHandler(override val entity: Entity) : TickableEntityHandler {

    override fun tick() {
        entity.gravityTickCount = if (entity.isOnGround) 0 else entity.gravityTickCount + 1
    }
}