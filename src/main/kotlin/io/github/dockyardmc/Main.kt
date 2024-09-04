package io.github.dockyardmc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.WorldManager

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if(args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugScoreboard.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }


    Commands.add("/explode") {
        addArgument("player", PlayerArgument())
        withPermission("player.admin")
        withDescription("executes stuff")
        execute {
            val executingPlayer = it.getPlayerOrThrow()
            val player = getArgument<Player>("player")

            player.spawnParticle(player.location, Particles.EXPLOSION_EMITTER, Vector3f(1f), amount = 5)
            player.playSound("minecraft:entity.generic.explode", volume = 2f, pitch = MathUtils.randomFloat(0.6f, 1.3f))

            player.sendMessage("<yellow>You got <rainbow><b>totally exploded <yellow>by <red>$executingPlayer")
            executingPlayer.sendMessage("<yellow>You <rainbow><b>totally exploded <yellow>player <red>$player")
        }
    }

    val server = DockyardServer()
    server.start()
}