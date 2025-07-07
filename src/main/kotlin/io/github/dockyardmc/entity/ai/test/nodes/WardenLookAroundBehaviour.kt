package io.github.dockyardmc.entity.ai.test.nodes

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.Warden
import io.github.dockyardmc.entity.WardenAnimation
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.sounds.Sound
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class WardenLookAroundBehaviour : EntityBehaviourNode() {

    override fun getScorer(entity: Entity): Float {
        return 0.04f
    }

    override fun onStart(entity: Entity) {
        (entity as Warden).playAnimation(WardenAnimation.SNIFF)
        entity.playSoundToViewers(Sound(Sounds.ENTITY_WARDEN_SNIFF))
        cooldown = Random.nextInt(100, 200)
        entity.world.scheduler.runLater(4.seconds) {
            this.getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
        }
    }

    override fun onBackstageTick(tick: Int) {
    }

    override fun onFrontstageTick(tick: Int) {
    }

    override fun onGeneralTick(tick: Int) {
    }

    override fun onStop(entity: Entity, interrupted: Boolean) {
    }
}