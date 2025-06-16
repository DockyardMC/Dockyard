package io.github.dockyardmc.entity.ai.test

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourCoordinator
import io.github.dockyardmc.entity.ai.test.nodes.WardenWalkAroundBehaviour

class WardenBehaviourCoordinator(entity: Entity) : EntityBehaviourCoordinator(entity) {

    init {
        behaviours.add(WardenWalkAroundBehaviour(this))
    }

}