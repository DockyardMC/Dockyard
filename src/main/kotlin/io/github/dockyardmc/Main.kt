package io.github.dockyardmc

import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.commands.CommandExecutor
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import kotlin.random.Random

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
            debug = true
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

    val altWorld = WorldManager.create("altworld", FlatWorldGenerator(Biomes.BASALT_DELTAS), DimensionTypes.NETHER)

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

    val poolMain = EventPool.withFilter("mainworldpool") { it.context.contains(WorldManager.mainWorld) || it.context.isGlobalEvent }
    val poolAlt = EventPool.withFilter("altworldpool") { it.context.contains(altWorld) || it.context.isGlobalEvent }

    poolMain.on<PlayerBlockBreakEvent> { it.cancelled = true }
    poolAlt.on<PlayerBlockBreakEvent> { DockyardServer.broadcastMessage("Player blocked in alt world.") }


    val customPool = EventPool(parent = null, name = "custompool")
    customPool.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] pool -> custom event! ${it.int++}") }

    val subPool = EventPool(parent = customPool, name = "subpool-1")
    subPool.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] subpool1 -> custom event! ${it.int++}") }

    val subPool2 = EventPool(parent = subPool, name = "subpool-2")
    subPool2.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] subpool2 -> custom event! ${it.int++}") }
    poolAlt.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] openpool -> custom event! ${it.int++}") }

    Commands.add("eventtest") {
        addArgument("area", StringArgument()) { listOf("open", "0", "1", "2", "unregister") }
        execute {
            val area = getArgument<String>("area")
            val event = CustomEvent()
            when (area) {
                "open" -> Events
                "0" -> customPool
                "1" -> subPool
                "2" -> subPool2
                "unregister" -> {
                    subPool2.dispose(); return@execute
                }
                else -> return@execute it.sendMessage(customPool.debugTree())
            }.dispatch(event)
            it.sendMessage("dispatched ${event.value}.")
        }
    }

    server.start()
}

class CustomEvent(val value: Int = Random.nextInt(0, 99), var int: Int = 0) : Event {
    override val context = Event.Context(isGlobalEvent = true)
}