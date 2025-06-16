package io.github.dockyardmc.entity.ai.test.nodes

import de.metaphoriker.pathetic.api.pathing.result.PathfinderResult
import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.Warden
import io.github.dockyardmc.entity.WardenAnimation
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.entity.ai.test.WardenBehaviourCoordinator
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.debug
import kotlin.time.Duration.Companion.seconds

class WardenSoundInvestigateBehaviour(val coordinator: WardenBehaviourCoordinator) : EntityBehaviourNode() {

    private var pathfindResultListener: ((PathfinderResult) -> Unit)? = null
    private var pathfindingStepListener: ((Navigator.PathfindingStep) -> Unit)? = null
    private var pathfindingEndListener: ((Navigator.PathfindingStep) -> Unit)? = null

    //TODO base this on current distance
    override fun getScorer(entity: Entity): Float {
        return if (coordinator.heardSoundInvestigationLocation == null) 0f else 1f
    }

    override fun onStart(entity: Entity) {
        coordinator.navigator.dispose()

        coordinator.navigator.pathfindResultDispatcher.subscribe { result ->
            if (result.hasFailed()) {
                getBehaviourFuture().complete(EntityBehaviourResult.FAILED)
                coordinator.heardSoundInvestigationLocation = null
                debug("<red>FAILED INVESTIGATE", true)
            } else {
                debug("<dark_green>found path", true)
            }
        }

        (entity as Warden).playAnimation(WardenAnimation.ROAR)
        entity.playSoundToViewers(Sound(Sounds.ENTITY_WARDEN_ROAR))
        entity.lookAt(coordinator.heardSoundInvestigationLocation!!)

        entity.world.scheduler.runLater(4.seconds) {
            coordinator.navigator.updatePathfindingPath(coordinator.heardSoundInvestigationLocation!!)
        }

        coordinator.navigator.navigationNodeStepDispatcher.subscribe {
            entity.playSoundToViewers(Sound(Sounds.ENTITY_WARDEN_STEP))
        }

        coordinator.navigator.navigationCompleteDispatcher.subscribe {
            getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
            coordinator.heardSoundInvestigationLocation = null
        }
    }

    override fun onBackstageTick(tick: Int) {
    }

    override fun onFrontstageTick(tick: Int) {
    }

    override fun onGeneralTick(tick: Int) {
    }

    override fun onStop(entity: Entity, interrupted: Boolean) {
        coordinator.navigator.dispose()
    }
}