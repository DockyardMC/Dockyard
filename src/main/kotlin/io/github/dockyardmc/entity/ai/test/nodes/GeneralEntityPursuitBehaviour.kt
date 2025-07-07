package io.github.dockyardmc.entity.ai.test.nodes

import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.entity.ai.test.SculkZombieBehaviourCoordinator
import io.github.dockyardmc.entity.metadata.EntityMetaValue
import io.github.dockyardmc.entity.metadata.EntityMetadata
import io.github.dockyardmc.entity.metadata.EntityMetadataType
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.maths.randomFloat
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import kotlin.random.Random

class GeneralEntityPursuitBehaviour(val coordinator: SculkZombieBehaviourCoordinator) : EntityBehaviourNode() {

    companion object {
        const val ATTACK_COOLDOWN = 20
        const val PATH_UPDATE_PERIOD_NORMAL = 1
        const val PATH_UPDATE_PERIOD_IDLE = 10
        const val PATH_UPDATE_FAIL_THRESHOLD = 6
        const val LOCATION_DIFFERENCE_THRESHOLD = 1.0
    }

    override val interruptible: Boolean = true
    private var pathfindResultListener: ((PathfinderResult) -> Unit)? = null
    private var pathfindingStepListener: ((Navigator.PathfindingStep) -> Unit)? = null
    private var pathfindingEndListener: ((Navigator.PathfindingStep) -> Unit)? = null

    var fails = 0
    var updateFrequency = PATH_UPDATE_PERIOD_NORMAL

    override fun getScorer(entity: Entity): Float {
        return if (coordinator.target == null) 0f else 0.9f // not 1f cause there might be bigger priority goal like going to cover or healing for other entities
    }

    override fun onStart(entity: Entity) {
        entity.metadata[EntityMetadataType.ARMOR_STAND_BITMASK] = EntityMetadata(EntityMetadataType.ARMOR_STAND_BITMASK, EntityMetaValue.BYTE, 0x04)
        pathfindResultListener = coordinator.navigator.pathfindResultDispatcher.subscribe { result ->
            if (result.hasFailed()) {
                fails++
                if (fails >= PATH_UPDATE_FAIL_THRESHOLD && updateFrequency != PATH_UPDATE_PERIOD_IDLE) {
                    updateFrequency = PATH_UPDATE_PERIOD_IDLE
                }
            } else {
                if (updateFrequency != PATH_UPDATE_PERIOD_NORMAL) {
                    updateFrequency = PATH_UPDATE_PERIOD_NORMAL
                    fails = 0
                }
            }
        }

        pathfindingEndListener = coordinator.navigator.navigationCompleteDispatcher.subscribe {
            getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
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
        }
    }

    override fun onGeneralTick(tick: Int) {
    }

    override fun onStop(entity: Entity, interrupted: Boolean) {
        entity.metadata[EntityMetadataType.ARMOR_STAND_BITMASK] = EntityMetadata(EntityMetadataType.ARMOR_STAND_BITMASK, EntityMetaValue.BYTE, 0x0)
        coordinator.navigator.cancelNavigating()
        pathfindingStepListener?.let { coordinator.navigator.navigationNodeStepDispatcher.unsubscribe(it) }
        pathfindingEndListener?.let { coordinator.navigator.navigationCompleteDispatcher.unsubscribe(it) }
        pathfindResultListener?.let { coordinator.navigator.pathfindResultDispatcher.unsubscribe(it) }
    }
}