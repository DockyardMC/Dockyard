package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.entity.TestZombie
import io.github.dockyardmc.entity.Warden
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.events.PlayerSpawnEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ConsumableAnimation
import io.github.dockyardmc.item.ConsumableItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.utils.randomInt
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html

fun main(args: Array<String>) {

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
            applyBlockPlacementRules = true
        }
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugSidebar.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, -1, 0, false)

        val item = ItemStack(Items.SWEET_BERRIES, 10)
        item.components.add(ConsumableItemComponent(2f, ConsumableAnimation.EAT, Sound(Sounds.ENTITY_GENERIC_EAT), true, listOf()))

        player.give(item)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }

    Commands.add("/reset") {
        addArgument("player", PlayerArgument(), simpleSuggestion(""))
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
