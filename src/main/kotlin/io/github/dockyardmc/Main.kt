package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Ravager
import io.github.dockyardmc.entity.TestZombie
import io.github.dockyardmc.events.*
import io.github.dockyardmc.extentions.broadcastMessage
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemRarity
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.pathfinding.Navigator
import io.github.dockyardmc.pathfinding.PatheticPlatformDockyard
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.utils.DebugSidebar
import io.github.dockyardmc.world.WorldManager

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

    var navigator: Navigator? = null
    var player: Player? = null
    var lastPlayerPos: Location? = null

    Commands.add("/startNavigating") {
        addArgument("speed", IntArgument())
        execute { ctx ->
            val cmdPlayer = ctx.getPlayerOrThrow()

            start = Location(1, 1, 29, cmdPlayer.world)
            end = Location(18, 0, 16, cmdPlayer.world)

            val startPathPos = PatheticPlatformDockyard.toPathPosition(start!!)
            val endPathPos = PatheticPlatformDockyard.toPathPosition(end!!)

            val entity = cmdPlayer.world.spawnEntity(Ravager(start!!)) as Ravager
            navigator = Navigator(entity, getArgument("speed"))
            navigator!!.updatePathfindingPath(cmdPlayer.location.getBlockLocation().subtract(0, 1, 0))
            player = cmdPlayer
            lastPlayerPos = player!!.location
        }
    }

    var i = 0
    Events.on<WorldTickEvent> { event ->
        i++
        if(i % 1 != 0) return@on
        if(player == null || navigator == null) return@on
        if(event.world == player!!.world) {
            if(lastPlayerPos != null && lastPlayerPos!!.distance(player!!.location) < 1.5) return@on
            lastPlayerPos = player!!.location
            navigator!!.updatePathfindingPath(player!!.location.getBlockLocation().subtract(0, 1, 0))
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
