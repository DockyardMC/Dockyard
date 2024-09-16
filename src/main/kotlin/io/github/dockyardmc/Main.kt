package io.github.dockyardmc

import io.github.dockyardmc.commands.*
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.resourcepack.addResourcepack
import io.github.dockyardmc.resourcepack.removeResourcepack
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

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

    Commands.add("/backtrack") {
        execute {
            if (pathStart == null || pathEnd == null) throw CommandException("start or end is null!")
            if (pathfinder == null) pathfinder = Pathfinder(pathStart!!, pathEnd!!)
            pathfinder!!.backTrack()
        }
    }

    Commands.add("/next") {
        execute {
            if (pathStart == null || pathEnd == null) throw CommandException("start or end is null!")
            if (pathfinder == null) pathfinder = Pathfinder(pathStart!!, pathEnd!!)
            pathfinder!!.nextStep()
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

    Commands.add("/totem") {
        execute {
            it.getPlayerOrThrow().playTotemAnimation(1)
        }
    }

    Commands.add("/resourcepack") {
        addSubcommand("add") {
            addArgument("name", StringArgument(), getPackSuggestions())
            addArgument("url", StringArgument())
            execute {
                val player = it.getPlayerOrThrow()
                val name = getArgument<String>("name")
                val url = getArgument<String>("url")

                DockyardServer.broadcastMessage(url)

                player.addResourcepack(name) {
                    withUrl(url)

                    onSuccess { response ->
                        response.player.sendMessage("<lime>Successfully loaded resourcepack!")
                    }
                    onFail { response ->
                        response.player.sendMessage("<red>womp womp: <orange>${response.status.name}")
                    }
                }
            }
        }

        addSubcommand("remove") {
            addArgument("name", StringArgument(), getPackSuggestions())
            execute {
                val player = it.getPlayerOrThrow()
                val name = getArgument<String>("name")

                player.removeResourcepack(name)
            }
        }
    }

    val server = DockyardServer()
    server.start()
}