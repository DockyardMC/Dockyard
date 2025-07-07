package io.github.dockyardmc.entity.ai.test.nodes

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.TestZombie
import io.github.dockyardmc.entity.ai.EntityBehaviourNode
import io.github.dockyardmc.entity.ai.EntityBehaviourResult
import io.github.dockyardmc.entity.ai.test.SculkZombieBehaviourCoordinator
import io.github.dockyardmc.protocol.packets.play.clientbound.ClientboundPlayerAnimationPacket
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityAnimation
import io.github.dockyardmc.registry.DamageTypes
import kotlin.time.Duration.Companion.seconds

class GenericEntityAttackPlayerBehaviourNode(val coordinator: SculkZombieBehaviourCoordinator) : EntityBehaviourNode() {

    override fun getScorer(entity: Entity): Float {
        if (coordinator.target == null) return 0f
        return if (entity.location.distance(coordinator.target!!.location) < 1.5) 1f else 0f
    }

    override fun onStart(entity: Entity) {
        val target = coordinator.target
        if (target == null) {
            getBehaviourFuture().complete(EntityBehaviourResult.FAILED)
            return
        }
        entity.sendPacketToViewers(ClientboundPlayerAnimationPacket(entity, EntityAnimation.SWING_MAIN_ARM))

        target.damage(2f, DamageTypes.GENERIC, entity)
        cooldown = 1.seconds
        getBehaviourFuture().complete(EntityBehaviourResult.SUCCESS)
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