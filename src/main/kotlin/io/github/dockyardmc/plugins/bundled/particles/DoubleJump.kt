package io.github.dockyardmc.plugins.bundled.particles

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.runnables.timedSequenceAsync
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.utils.Vector3f

class DoubleJump {

    init {

        Events.on<PlayerFlightToggleEvent> {
            it.cancelled = true
            val player = it.player
            player.canFly.value = true

            timedSequenceAsync { seq ->
                player.playSound(Sound("entity.bat.takeoff", player.location, 0.5f, 1.3f))
                player.world.spawnParticle(player.location, Particles.CLOUD, count = 5, offset = Vector3f(0.5f), speed = 0.1f)

                seq.wait(25.ticks)

                player.sendMessage("<rainbow>yo how is it going, you should see this 25 ticks later :3")
                player.setEntityVelocity(Vector3(10, 0, 30))
            }
        }
    }
}