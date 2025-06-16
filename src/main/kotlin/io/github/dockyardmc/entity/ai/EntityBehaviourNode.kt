package io.github.dockyardmc.entity.ai

import io.github.dockyardmc.entity.Entity
import java.util.concurrent.CompletableFuture

abstract class EntityBehaviourNode {
    open val interruptible: Boolean = true
    private var future = CompletableFuture<EntityBehaviourResult>()

    fun getBehaviourFuture(): CompletableFuture<EntityBehaviourResult> = future
    fun setBehaviourFuture(newFuture: CompletableFuture<EntityBehaviourResult>) {
        this.future = newFuture
    }

    abstract fun getScorer(entity: Entity): Float

    abstract fun onStart(entity: Entity)

    abstract fun onBackstageTick(tick: Int)
    abstract fun onFrontstageTick(tick: Int)
    abstract fun onGeneralTick(tick: Int)

    abstract fun onStop(entity: Entity, interrupted: Boolean)
}