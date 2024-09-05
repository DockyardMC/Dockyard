package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.add
import io.github.dockyardmc.player.toPersistent
import io.github.dockyardmc.protocol.packets.play.clientbound.EntityEquipmentLayer
import io.github.dockyardmc.protocol.packets.play.serverbound.placementRules
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f

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


    Commands.add("/equipment") {
        addArgument("player", PlayerArgument())
        execute {
            val player = it.getPlayerOrThrow()
            val target = getArgument<Player>("player")

            target.equipmentLayers[player.toPersistent()] = EntityEquipmentLayer(
                helmet = ItemStack(Items.LIME_STAINED_GLASS),
                offHand = ItemStack(Items.LEAD),
                boots = ItemStack(Items.NETHERITE_BOOTS)
            )
        }
    }

    Events.on<PlayerDropItemEvent> {
        it.cancelled = true
    }


    val server = DockyardServer()
    server.start()
}