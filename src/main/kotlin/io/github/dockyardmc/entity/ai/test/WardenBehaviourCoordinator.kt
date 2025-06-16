package io.github.dockyardmc.entity.ai.test

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourCoordinator
import io.github.dockyardmc.entity.ai.test.nodes.WardenLookAroundBehaviour
import io.github.dockyardmc.entity.ai.test.nodes.WardenSoundInvestigateBehaviour
import io.github.dockyardmc.entity.ai.test.nodes.WardenWalkAroundBehaviour
import io.github.dockyardmc.location.Location

class WardenBehaviourCoordinator(entity: Entity) : EntityBehaviourCoordinator(entity) {

    var heardSoundInvestigationLocation: Location? = null

    init {
        behaviours.add(WardenWalkAroundBehaviour(this))
        behaviours.add(WardenLookAroundBehaviour())
        behaviours.add(WardenSoundInvestigateBehaviour(this))
    }

}