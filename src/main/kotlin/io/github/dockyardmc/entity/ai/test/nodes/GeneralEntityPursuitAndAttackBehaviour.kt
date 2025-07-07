package io.github.dockyardmc.entity.ai.test.nodes

import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult
import io.github.dockyardmc.DockyardServer
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.test.SculkZombieBehaviourCoordinator
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import kotlin.random.Random

class GeneralEntityPursuitAndAttackBehaviour(val coordinator: SculkZombieBehaviourCoordinator) : EntityBehaviourNode() {

    companion object {
        const val ATTACK_COOLDOWN = 20
        const val PATH_UPDATE_PERIOD_NORMAL = 1
        const val PATH_UPDATE_PERIOD_IDLE = 10
        const val PATH_UPDATE_FAIL_THRESHOLD = 6
        const val LOCATION_DIFFERENCE_THRESHOLD = 1.0
    }

    private var pathfindResultListener: ((PathfinderResult) -> Unit)? = null
    private var pathfindingStepListener: ((Navigator.PathfindingStep) -> Unit)? = null
    private var pathfindingEndListener: ((Navigator.PathfindingStep) -> Unit)? = null

    var fails = 0
    var updateFrequency = PATH_UPDATE_PERIOD_NORMAL

    override fun getScorer(entity: Entity): Float {
        return if (coordinator.target == null) 0f else 0.9f // not 1f cause there might be bigger priority goal like going to cover or healing for other entities
    }

    override fun onStart(entity: Entity) {
        pathfindResultListener = coordinator.navigator.pathfindResultDispatcher.subscribe { result ->
            if (result.hasFailed()) {
                fails++
                broadcastMessage("<red>failed ($fails)")
                if (fails >= PATH_UPDATE_FAIL_THRESHOLD && updateFrequency != PATH_UPDATE_PERIOD_IDLE) {
                    updateFrequency = PATH_UPDATE_PERIOD_IDLE
                    broadcastMessage("<orange>Switching to idle period <yellow>$updateFrequency")
                }
            } else {
                broadcastMessage("<lime>Found: ${result.path.length()}")
                if (updateFrequency != PATH_UPDATE_PERIOD_NORMAL) {
                    updateFrequency = PATH_UPDATE_PERIOD_NORMAL
                    fails = 0
                    broadcastMessage("<orange>Switching to normal period <yellow>$updateFrequency")
                }
            }
        }

        pathfindingEndListener = coordinator.navigator.navigationCompleteDispatcher.subscribe {
            broadcastMessage("<lime><bold>finished walking")
        }

        pathfindingStepListener = coordinator.navigator.navigationNodeStepDispatcher.subscribe {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_ZOMBIE_STEP, volume = 0.1f, pitch = Random.randomFloat(1f, 1.3f)), entity.location)
        }
    }

    override fun onBackstageTick(tick: Int) {
    }

    var lastTargetLocation: Location? = null
    var tick: Int = 0

    override fun onFrontstageTick(tick: Int) {
        this.tick++
        if (coordinator.target == null) return
        var shouldPathfind = false
        if (lastTargetLocation == null) shouldPathfind = true
        if (lastTargetLocation != null && lastTargetLocation!!.distance(coordinator.target!!.location) > LOCATION_DIFFERENCE_THRESHOLD) shouldPathfind = true
        if (tick % updateFrequency != 0) shouldPathfind = false

        if (shouldPathfind) {
            lastTargetLocation = coordinator.target!!.location
            val closestGroundNode = coordinator.target!!.location.closestSolidBelow ?: return
            coordinator.navigator.updatePathfindingPath(closestGroundNode.second)
            DockyardServer.broadcastMessage("<gray>Updated pathfinding target ($tick)")
        }

        //TODO attack when close
        // maybe this should be different behaviour node?
    }

    override fun onGeneralTick(tick: Int) {
    }

    override fun onStop(entity: Entity, interrupted: Boolean) {
        pathfindingStepListener?.let { coordinator.navigator.navigationNodeStepDispatcher.unsubscribe(it) }
        pathfindingEndListener?.let { coordinator.navigator.navigationCompleteDispatcher.unsubscribe(it) }
        pathfindResultListener?.let { coordinator.navigator.pathfindResultDispatcher.unsubscribe(it) }
    }
}