package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.EnumArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.ItemDropEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerLeaveEvent
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.ui.examples.ExampleCookieClickerScreen
import io.github.dockyardmc.ui.examples.ExampleMinesweeperScreen
import io.github.dockyardmc.utils.DebugScoreboard

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
        }
    }

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

        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
    }

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }

    val altWorld = WorldManager.create("altworld", FlatWorldGenerator(Biomes.BASALT_DELTAS), DimensionTypes.NETHER)

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

    Events.on<CommandExecuteEvent> {
        if (!it.raw.startsWith("eventtest register_new")) return@on

        val newPool = EventPool(name = "new_pool")
        newPool.on<CommandExecuteEvent> { evt -> DockyardServer.broadcastMessage("Did command ${evt.raw}!!!") }
        DockyardServer.broadcastMessage("registered new")
    }

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
                "register_new" -> {
                }
                else -> return@execute it.sendMessage(customPool.debugTree())
            }
            it.sendMessage("dispatched ${event.value}.")

    Events.on<PlayerDropItemEvent> {
        it.cancelled = true
    }

    Commands.add("/drop") {
        execute {
            val player = it.getPlayerOrThrow()
            val entity = player.world.spawnEntity(ItemDropEntity(player.location, ItemStack(Items.DIAMOND, 1))) as ItemDropEntity
            entity.autoViewable = true
        }
    }

    Commands.add("/minigame") {
        addSubcommand("cookie_clicker") {
            execute {
                val player = it.getPlayerOrThrow()
                player.openInventory(ExampleCookieClickerScreen(player))
            }
        }

        addSubcommand("minigame") {
            execute {
                val player = it.getPlayerOrThrow()
                player.openInventory(ExampleMinesweeperScreen(player, 10))
            }
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