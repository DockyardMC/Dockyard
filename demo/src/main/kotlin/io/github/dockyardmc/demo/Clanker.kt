package io.github.dockyardmc.demo

import io.github.dockyardmc.entity.ai.EntityBehaviourCoordinator
import io.github.dockyardmc.entity.entities.CopperGolem
import io.github.dockyardmc.location.Location

class Clanker(location: Location, val designedLocation: Location) : CopperGolem(location) {

    val brain = ClankerBehaviourCoordinator(this)

    override fun tick() {
        super.tick()
        brain.tick()
    }

}

class ClankerBehaviourCoordinator(entity: Clanker) : EntityBehaviourCoordinator(entity) {

    init {
        this.behaviours.add(WalkToSpotBehaviourNode(this, entity.designedLocation))
    }

}