package io.github.dockyardmc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.entities.Parrot
import io.github.dockyardmc.entities.TestZombie
import io.github.dockyardmc.entities.Warden
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.randomInt
import io.github.dockyardmc.world.WorldManager
import java.lang.IllegalStateException

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
        }
    }

    Commands.add("/entity") {
        execute {
            val random = randomInt(1, 3)
            val player = it.getPlayerOrThrow()
            val entity = when(random) {
                1 -> TestZombie(player.location)
                2 -> Warden(player.location)
                3 -> Parrot(player.location)
                else -> throw IllegalStateException("a")
            }

            player.world.spawnEntity(entity)
        }
    }

    Commands.add("/vehicletp") {
        execute {
            val player = it.getPlayerOrThrow()
            if(player.vehicle == null) return@execute
            val random = player.location.add(randomInt(-5, 5), 0, randomInt(-5, 5))

            player.vehicle!!.teleport(random)
        }
    }

    Events.on<PlayerInteractWithEntityEvent> {
        it.entity.passengers.add(it.player)
    }

    Events.on<EntityDismountVehicleEvent> {
        if(it.passenger !is Player) return@on
        it.passenger.sendMessage("<red>Dismounted vehicle")
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

    Events.on<PlayerRightClickWithItemEvent> {
        it.player.setCooldown(it.item.material, 200)
    }

    Events.on<ItemGroupCooldownStartEvent> {
        it.player.sendMessage("<lime>${it.cooldown.group} started for ${it.cooldown.durationTicks}")
    }

    Events.on<ItemGroupCooldownEndEvent> {
        it.player.sendMessage("<red>${it.cooldown.group} ended (lasted ${it.cooldown.durationTicks})")
    }

    Commands.add("/greedy") {
        addArgument("player", PlayerArgument())
        addArgument("world", WorldArgument())
        addArgument("message", StringArgument(BrigadierStringType.GREEDY_PHRASE))
        execute {
            val player = it.getPlayerOrThrow()
            val message = getArgument<String>("message")
            player.sendMessage("You said: <rainbow>$message")
        }
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
