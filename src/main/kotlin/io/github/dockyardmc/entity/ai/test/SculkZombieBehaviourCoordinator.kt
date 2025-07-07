package io.github.dockyardmc.entity.ai.test

import io.github.dockyardmc.entity.TestZombie
import io.github.dockyardmc.entity.ai.EntityBehaviourCoordinator
import io.github.dockyardmc.entity.ai.test.nodes.GeneralEntityPursuitBehaviour
import io.github.dockyardmc.entity.ai.test.nodes.GenericEntityAttackPlayerBehaviourNode
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.WorldTickEvent
import io.github.dockyardmc.maths.vectors.Vector3f
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Particles
import kotlin.random.Random

class SculkZombieBehaviourCoordinator(val zombie: TestZombie) : EntityBehaviourCoordinator(zombie) {

    var target: Player? = null

    init {
        behaviours.add(GeneralEntityPursuitBehaviour(this))
        behaviours.add(GenericEntityAttackPlayerBehaviourNode(this))

        navigator.speedTicksPerBlock = 8

        navigator.navigationNodeStepDispatcher.subscribe {
            zombie.world.spawnParticle(zombie.location.add(0f, 0.3f, 0f), Particles.SCULK_SOUL, offset = Vector3f(0.1f), amount = Random.nextInt(1, 3), speed = 0f)
        }

        Events.on<WorldTickEvent> { event ->
            if (event.world != zombie.world) return@on

            val playersInArea = zombie.world.players.filter { player ->
                player.location.distance(zombie.location) <= 15
                        && !player.isFlying.value
                        && player.location.block.registryBlock != Blocks.WATER
            }

            if (target != null && playersInArea.isEmpty()) {
                target = null
                return@on
            }

            val player = playersInArea.firstOrNull() ?: return@on
            if (player.isFlying.value) return@on
            target = player
        }
    }
}