package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.*
import io.github.dockyardmc.events.system.EventFilter
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator
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

    val poolMain = EventPool.withFilter("mainworldpool", EventFilter.containsWorld(WorldManager.mainWorld))
    val poolAlt = EventPool.withFilter("altworldpool", EventFilter.containsWorld(altWorld))

    poolMain.on<PlayerBlockBreakEvent> { it.cancelled = true }
    poolAlt.on<PlayerBlockBreakEvent> { DockyardServer.broadcastMessage("Player blocked in alt world.") }

    val customPool = EventPool(parent = null, name = "custompool")
    customPool.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] pool -> custom event! ${it.int++}") }

    val subPool = customPool.subPool("subpool-1")
    subPool.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] subpool1 -> custom event! ${it.int++}") }

    val subPool2 = subPool.subPool("subpool-2")
    subPool2.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] subpool2 -> custom event! ${it.int++}") }
    poolAlt.on<CustomEvent> { DockyardServer.broadcastMessage("[${it.value}] openpool -> custom event! ${it.int++}") }

    Commands.add("eventtest") {
        addArgument("area", StringArgument()) { listOf("open", "0", "1", "2", "unregister", "fork") }
        execute {
            val area = getArgument<String>("area")
            val event = CustomEvent()
            when (area) {
                "open" -> Events.dispatch(event)
                "0" -> customPool.dispatch(event)
                "1" -> subPool.dispatch(event)
                "2" -> subPool2.dispatch(event)
                "unregister" -> {
                    subPool2.dispose()
                }
                "fork" -> {
                    subPool.fork()
                }
                "clear_children" -> {
                    customPool.clearChildren()
                }
                "unregister_listeners" -> {
                    poolAlt.unregisterAllListeners()
                }
                else -> return@execute it.sendMessage(customPool.debugTree())
            }
            it.sendMessage("dispatched ${event.value}.")
        }
    }

    server.start()


    Commands.add("/displayname") {
        addArgument("name", StringArgument())
        execute {
            val player = it.getPlayerOrThrow()
            val name = getArgument<String>("name")

            player.displayName.value = name
        }
    }
}

class CustomEvent(val value: Int = Random.nextInt(0, 99), var int: Int = 0) : Event {
    override val context = Event.Context(isGlobalEvent = true)
}