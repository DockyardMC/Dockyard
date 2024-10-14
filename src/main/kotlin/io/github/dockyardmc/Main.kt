package io.github.dockyardmc

import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.Chunk
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
        useDebugMode(false)
        withImplementations {
            dockyardCommands = true
            npcCommand = true
        }
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

    var pos1: Location? = null
    var pos2: Location? = null


    Events.on<PlayerBlockRightClickEvent> {
        if(it.heldItem.material != Items.DEBUG_STICK) return@on
        pos1 = it.location
        it.player.sendMessage("<lime>Pos1 set")
    }

    Events.on<PlayerBlockBreakEvent> {
        if(it.player.getHeldItem(PlayerHand.MAIN_HAND).material != Items.DEBUG_STICK) return@on
        pos2 = it.location
        it.player.sendMessage("<lime>Pos2 set")
    }

    Commands.add("/bound") {
        execute {
            val player = it.getPlayerOrThrow()
            val bound = Bound(pos1!!.getBlockLocation(), pos2!!.getBlockLocation())
            player.sendMessage("$bound")

            bound.getBlocks().forEach { loop ->
                player.world.setBlock(loop.key, Blocks.RED_STAINED_GLASS)
            }

            bound.onEnter { pl ->
                pl.sendMessage("<green>Welcome to bound!!")
            }

            bound.onLeave { pl ->
                pl.sendMessage("<red>goobye :c")
            }
        }
    }

    Events.on<PlayerEnterBoundEvent> {
        if(it.player.username != "LukynkaCZE") it.cancelled = true
    }

    Commands.add("/reset") {
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld
            val chunks = mutableListOf<Chunk>()

            for (x in 0 until platformSize) {
                for (z in 0 until platformSize) {
                    world.setBlock(x, 0, z, Blocks.STONE)
                    val chunk = world.getChunkAt(x, z)!!
                    if (!chunks.contains(chunk)) chunks.add(chunk)
                    for (y in 1 until 20) {
                        world.setBlockRaw(x, y, z, Blocks.AIR.defaultBlockStateId, false)
                    }
                }
            }
        }
    }

    server.start()
}