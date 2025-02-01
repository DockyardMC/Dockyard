package io.github.dockyardmc

import de.metaphoriker.pathetic.api.pathing.filter.filters.PassablePathFilter
import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.TestZombie
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemRarity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard
import io.github.dockyardmc.pathfinding.RequiredHeightPathfindingFilter
import io.github.dockyardmc.pathfinding.Pathfinder
import io.github.dockyardmc.pathfinding.PathfindingHelper
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.runnables.ticks
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematicAsync
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.WorldManager
import java.io.File

// This is just testing/development environment.
// To properly use dockyard, visit https://dockyardmc.github.io/Wiki/wiki/quick-start.html
var start: Location? = null
var end: Location? = null

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

    Events.on<PlayerLeaveEvent> {
        DockyardServer.broadcastMessage("<yellow>${it.player} left the game.")
    }




    Events.on<PlayerBlockRightClickEvent> { event ->
        if (event.player.getHeldItem(PlayerHand.MAIN_HAND).material != Items.STICK) return@on
        start = event.location
        event.player.sendMessage("<lime>Selected start position!")
        start!!.world.setBlock(start!!, Blocks.DIAMOND_BLOCK)
        event.cancelled = true
    }

    Events.on<PlayerBlockBreakEvent> { event ->
        if (event.player.getHeldItem(PlayerHand.MAIN_HAND).material != Items.STICK) return@on
        end = event.location
        end!!.world.setBlock(end!!, Blocks.EMERALD_BLOCK)
        event.player.sendMessage("<lime>Selected end position!")
        event.cancelled = true
    }

    var schemPlaced: Boolean = false

    Commands.add("/testPathfinding") {
        execute { ctx ->
            val player = ctx.getPlayerOrThrow()

            val middle = Location(0, 0, 0, player.world)
            val schematic = SchematicReader.read(File("./test.schem"))

            if (!schemPlaced) player.world.placeSchematicAsync(schematic, middle).thenAccept { schemPlaced = true; runPathfinding(player) } else runPathfinding(player)
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

fun runPathfinding(player: Player) {
    start = Location(1, 1, 29, player.world)
    end = Location(18, 0, 16, player.world)

    val startPathPos = PatheticPlatformDockyard.toPathPosition(start!!)
    val endPathPos = PatheticPlatformDockyard.toPathPosition(end!!)

    player.sendMessage("<gray>Starting pathfind... [Distance: ${start!!.distance(end!!)}]")
    val pathfindingResult = Pathfinder.defaultPathfinder.findPath(startPathPos, endPathPos, listOf(PassablePathFilter(), RequiredHeightPathfindingFilter()))
    pathfindingResult.thenAccept { result ->
        player.sendMessage("State: ${result.pathState.name}")
        player.sendMessage("Path length: ${result.path.length()}")

        if (result.successful() || result.hasFallenBack()) {
//            result.path.forEach { position ->
//                val location = PatheticPlatformDockyard.toLocation(position)
//                location.world.setBlock(location, Blocks.YELLOW_CONCRETE_POWDER)
//            }

            val zombie = player.world.spawnEntity(TestZombie(start!!.add(0, 1, 0))) as TestZombie

            val path = result.path
            path.forEachIndexed { index, position ->
                player.world.scheduler.runLater((index * 3).ticks) {
                    val next = path.toList().getOrNull(index + 1)
                    val nextLocation = if(next == null)  zombie.location else PatheticPlatformDockyard.toLocation(next).add(0.5, 1.0, 0.5)
                    zombie.teleport(PatheticPlatformDockyard.toLocation(position).add(0.5, 1.0, 0.5).setDirection(nextLocation.toVector3d() - (zombie.location).toVector3d()))
                }
            }

        } else {
            player.sendMessage("<red>Path not found!")
        }
    }
}