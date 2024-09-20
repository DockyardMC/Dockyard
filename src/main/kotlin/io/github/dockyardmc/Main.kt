package io.github.dockyardmc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.player.add
import io.github.dockyardmc.profiler.Profiler
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.addPotionEffect
import io.github.dockyardmc.runnables.timedSequenceAsync
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.randomFloat
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

    fun getPackSuggestions(): CommandSuggestions {
        return SuggestionProvider.withContext { it.resourcepacks.keys.toList() }
    }

//    val testWorld = WorldManager.create("test", FlatWorldGenerator(), DimensionTypes.OVERWORLD)
//    testWorld.defaultSpawnLocation = Location(0, 201, 0, testWorld)
//    Events.on<PlayerPreSpawnWorldSelectionEvent> {
//        it.world = testWorld
//    }

    var pathStart: Location? = null
    var pathEnd: Location? = null

    var pathfinder: Pathfinder? = null


    Commands.add("/reset") {
        execute {
            val platformSize = 30

            val world = WorldManager.mainWorld
            val chunks = mutableListOf<Chunk>()

            for (x in 0 until platformSize) {
                for (z in 0 until platformSize) {
                    world.setBlock(x, 0, z, Blocks.STONE)
                    val chunk = world.getChunkAt(x, z)!!
                    if(!chunks.contains(chunk)) chunks.add(chunk)
                    for (y in 1 until 20) {
                        world.setBlockRaw(x, y, z, Blocks.AIR.defaultBlockStateId, false)
                    }
                }
            }
            chunks.forEach { chunk ->
                chunk.updateCache()
                PlayerManager.players.sendPacket(chunk.packet)
            }
        }
    }


    Commands.add("/find") {
        execute {
            if (pathStart == null || pathEnd == null) throw CommandException("start or end is null!")
            pathfinder = Pathfinder(pathStart!!, pathEnd!!)
            val profiler = Profiler()
            profiler.start("Pathfind")
            val path = pathfinder!!.findPath() ?: throw CommandException("Path not found!")
            profiler.end()
            timedSequenceAsync { seq ->
                path.forEach {
                    it.world.setBlock(it, Blocks.YELLOW_CONCRETE)
                    it.world.players.values.playSound(
                        "minecraft:entity.chicken.egg",
                        pitch = randomFloat(1f, 2f)
                    )
                    seq.wait(1)
                }
            }
        }
    }

    Events.on<PlayerBlockRightClickEvent> {
        if (it.heldItem.material == Items.DEBUG_STICK) {
            it.player.playSound("minecraft:block.note_block.pling")
            it.player.sendMessage("<lime>First point set!")
            it.location.world.setBlock(it.location, Blocks.GOLD_BLOCK)
            pathStart = it.location
        }
    }

    Events.on<PlayerBlockBreakEvent> {
        if (it.player.getHeldItem(PlayerHand.MAIN_HAND).material == Items.DEBUG_STICK) {
            it.player.playSound("minecraft:block.note_block.pling")
            it.player.sendMessage("<lime>Second point set!")
            it.location.world.setBlock(it.location, Blocks.DIAMOND_BLOCK)
            pathEnd = it.location
        }
    }


    val server = DockyardServer()
    server.start()
}