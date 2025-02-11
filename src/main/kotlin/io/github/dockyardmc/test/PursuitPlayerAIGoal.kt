package io.github.dockyardmc.test

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.AIGoal
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.debug
import io.github.dockyardmc.utils.randomFloat

class PursuitPlayerAIGoal(override var entity: Entity, override var priority: Int, val targetUnit: () -> Entity?, val navigator: Navigator): AIGoal() {

    var hasFinishedWalking = false

    override fun startCondition(): Boolean {
        return targetUnit.invoke() != null
    }

    override fun start() {
        hasFinishedWalking = false

        navigator.onNavigationComplete {
            hasFinishedWalking = true
        }

        navigator.onNavigationNodeStep {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_RAVAGER_STEP, volume = 0.1f, pitch = randomFloat(1f, 1.3f)), entity.location)
        }
    }

    override fun end() {
        navigator.cancelNavigating()
    }

    override fun endCondition(): Boolean {
        return hasFinishedWalking
    }

    override fun tick() {
        val target = targetUnit.invoke()

        if(target == null) {
            hasFinishedWalking = true
            return
        }

        navigator.updatePathfindingPath(target.location.subtract(0, 1, 0))
    }
}