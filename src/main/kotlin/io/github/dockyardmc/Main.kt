package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.ItemArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.WorldManager

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

fun main(args: Array<String>) {

    if (args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if (args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    val server = DockyardServer {
        withIp("0.0.0.0")
        withMaxPlayers(50)
        withPort(25565)
        useMojangAuth(true)
        useDebugMode(true)
        withImplementations {
            dockyardCommands = true
            debug = true
            npcCommand = true
            itemDroppingAndPickup = true
        }
    }

    val list = listOf<String>()
    list.size

    Commands.add("equip") {
        addArgument("slot", EnumArgument(EquipmentSlot::class))
        execute {
            val player = it.getPlayerOrThrow()
            val slot = getEnumArgument<EquipmentSlot>("slot")
            player.equipment[slot] = player.getHeldItem(PlayerHand.MAIN_HAND)
        }
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugScoreboard.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, -1, 0, false)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }

    Commands.add("/slot") {
        addSubcommand("set") {
            addArgument("slot", IntArgument())
            addArgument("material", ItemArgument())
            execute {
                val player = it.getPlayerOrThrow()
                val slot = getArgument<Int>("slot")
                val material = getArgument<Item>("material")

                player.inventory[slot] = material.toItemStack()
            }
        }
    }

    Commands.add("/reset") {
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld

            world.batchBlockUpdate {
                for (x in 0 until platformSize) {
                    for (z in 0 until platformSize) {
                        setBlock(x, 0, z, Blocks.STONE)
                        for (y in 1 until 20) {
                            setBlock(x, y, z, Blocks.AIR)
                        }
                    }
                }
            }
        }
    }

    server.start()
}