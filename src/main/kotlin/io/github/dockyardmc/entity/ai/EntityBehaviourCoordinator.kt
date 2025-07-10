package io.github.dockyardmc.entity.ai

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.pathfinding.IsSolidPathFilter
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.RequiredHeightPathfindingFilter
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import io.github.dockyardmc.scheduler.runnables.ticks
import io.github.dockyardmc.utils.Disposable
import io.github.dockyardmc.utils.Freezable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

abstract class EntityBehaviourCoordinator(val entity: Entity) : Freezable(), Disposable {

    val eventPool = EventPool().withFilter(EventFilter.containsEntity(entity))
    val behaviours: MutableList<EntityBehaviourNode> = mutableListOf()
    var activeBehaviour: EntityBehaviourNode? = null
    private val isEvaluating = AtomicBoolean(false)
    val pathfinder = Pathfinder.createPathfinder {
        async(true)
        maxIterations(300)
        maxLength(60)
    }

    val navigator: Navigator = Navigator(entity, 15, pathfinder, listOf(RequiredHeightPathfindingFilter(2), IsSolidPathFilter()))

    var generalTicks = 0
    var frontStageTicks = 0
    var backstageTicks = 0

    override fun freeze() {
        super.freeze()
        navigator.cancelNavigating()
        stopBehaviour()
    }

    fun tick() {
        if (frozen) return
        generalTicks++
        backstageTicks++

        evaluateBehaviours()

        if (activeBehaviour?.getBehaviourFuture() != null && activeBehaviour?.getBehaviourFuture()!!.isDone) {
            stopBehaviour()
        }
        if (activeBehaviour != null) {
            frontStageTicks++
            activeBehaviour!!.onGeneralTick(generalTicks)
            activeBehaviour!!.onFrontstageTick(frontStageTicks)
        }
        behaviours.forEach { behaviour ->
            if (behaviour == activeBehaviour) return@forEach
            behaviour.onBackstageTick(backstageTicks)
            if (behaviour.cooldown.inWholeMinecraftTicks > 0) {
                behaviour.cooldown = behaviour.cooldown.minus(1.ticks)
            }
        }
    }

    fun stopBehaviour() {
        activeBehaviour?.onStop(entity, true)
        activeBehaviour?.getBehaviourFuture()?.cancel(true)
        activeBehaviour = null
    }

    fun forceBehaviour(behaviourNode: EntityBehaviourNode) {
        stopBehaviour()

        behaviourNode.setBehaviourFuture(CompletableFuture<EntityBehaviourResult>())
        this.activeBehaviour = behaviourNode
        behaviourNode.onStart(entity)
    }

    fun evaluateBehaviours() {
        if (isEvaluating.get()) return
        isEvaluating.set(true)

        val currentScorer = activeBehaviour?.getScorer(entity) ?: 0f

        try {
            val filtered = behaviours.filter { behaviour ->
                behaviour != activeBehaviour && behaviour.getScorer(entity) != 0f && behaviour.cooldown.inWholeMinecraftTicks == 0
            }
            val bestNode = filtered.maxByOrNull { behaviour -> behaviour.getScorer(entity) }
            if (bestNode != null) {
                if ((activeBehaviour != null && activeBehaviour!!.interruptible && bestNode.getScorer(entity) > currentScorer) || activeBehaviour == null) {
                    forceBehaviour(bestNode)
                }
            }

        } finally {
            isEvaluating.set(false)
        }
    }

    override fun dispose() {
        eventPool.dispose()
        activeBehaviour?.onStop(entity, true)
        activeBehaviour?.getBehaviourFuture()?.cancel(false)
    }

}