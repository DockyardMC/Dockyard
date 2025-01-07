package io.github.dockyardmc

import io.github.dockyardmc.apis.GuardianBeam
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.commands.simpleSuggestion
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.entity.Guardian
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemRarity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.WorldManager
import kotlin.time.Duration.Companion.seconds

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
        withNetworkCompressionThreshold(256)
        withImplementations {
            dockyardCommands = true
            debug = true
            npcCommand = true
            itemDroppingAndPickup = true
            applyBlockPlacementRules = true
        }
    }

    Events.on<PlayerBlockRightClickEvent> {
        it.player.playChestAnimation(it.location, Player.ChestAnimation.OPEN)
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player

        DockyardServer.broadcastMessage("<yellow>${player} joined the game.")
        player.gameMode.value = GameMode.CREATIVE
        player.permissions.add("dockyard.all")

        DebugSidebar.sidebar.viewers.add(player)

        player.addPotionEffect(PotionEffects.NIGHT_VISION, -1, 0, false)

        val item = ItemStack(Items.SWEET_BERRIES, 10).withConsumable(0.1f).withFood(2, 0f, true).withRarity(ItemRarity.EPIC)
        player.give(item)
    }

    var laser: GuardianBeam? = null
    Commands.add("/laser") {
        execute {
            val player = it.getPlayerOrThrow()
            laser = GuardianBeam(Location(0, 10, 0, player.world), player.location)
        }
    }

    Commands.add("/move") {
        execute {
            val player = it.getPlayerOrThrow()
            laser?.moveEnd(player.location, 1.seconds)
        }
    }

    Events.on<WorldTickEvent> { event ->
        val player = event.world.players.firstOrNull() ?: return@on
        if (laser?.end?.value == player.location) return@on
        laser?.moveEnd(player.location, 2.ticks)
    }

    Commands.add("/cleartarget") {
        execute {
            val player = it.getPlayerOrThrow()
            player.world.entities.forEach { entity ->
                if (entity is Guardian) {
                    entity.target.value = null
                    entity.isRetractingSpikes.value = true
                }
            }
        }
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