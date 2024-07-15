package io.github.dockyardmc.plugins.bundled.emberseeker.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.player.EntityPose
import io.github.dockyardmc.plugins.bundled.emberseeker.entities.EmberSeekerWarden

class WardenCommand {

    init {
        Commands.add("warden") {
            it.execute { exec ->
                val player = exec.playerOrThrow()
                val warden = EmberSeekerWarden(player.location, player.world)
                player.world.spawnEntity(warden)
                warden.pose.value = EntityPose.EMERGING
            }
        }
    }
}