package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.FloatArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.*
import io.github.dockyardmc.entities.EntityManager.spawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerPreSpawnWorldSelectionEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator

// This is just maya testing env.. do not actually run this
fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if(args.contains("event-documentation")) {
        EventsDocumentationGenerator()
        return
    }

    val testWorld = WorldManager.create("test", FlatWorldGenerator(), DimensionTypes.OVERWORLD)
    testWorld.defaultSpawnLocation = Location(0, 201, 0, testWorld)

    Events.on<PlayerPreSpawnWorldSelectionEvent> {
        it.world = testWorld
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player
        player.gameMode.value = GameMode.CREATIVE
        player.inventory[0] = Items.CHERRY_TRAPDOOR.toItemStack()
        DebugScoreboard.sidebar.viewers.add(player)
        player.addPotionEffect(PotionEffects.NIGHT_VISION, 99999, 0, false)
        player.addPotionEffect(PotionEffects.SPEED, 99999, 3, false)
    }

    Commands.add("/world") { cmd ->
        cmd.addArgument("world", StringArgument())
        cmd.execute { executor ->
            val player = executor.player!!
            val world = WorldManager.getOrThrow(cmd.get<String>("world"))
            world.join(player)
        }
    }

    var itemDisplay: BlockDisplay? = null

    Commands.add("/block") {
        it.execute { ctx ->
            val player = ctx.playerOrThrow()

            val world = player.world
            val location = player.location
            val entity = world.spawnEntity(BlockDisplay(location.withNoRotation(), world)) as BlockDisplay
            itemDisplay = entity
            entity.interpolationDelay.value = 0
            entity.transformInterpolation.value = 20
            entity.block.value = Blocks.CHERRY_PLANKS
        }
    }

    Commands.add("/scale") {
        it.addArgument("x", FloatArgument())
        it.addArgument("y", FloatArgument())
        it.execute { ctx ->
            val player = ctx.playerOrThrow()
            val x = it.get<Float>("x")
            val y = it.get<Float>("y")

            itemDisplay?.scale?.value = Vector3f(x, y, 5f)
        }
    }

    Commands.add("/rotate") {
        it.addArgument("x", FloatArgument())
        it.addArgument("y", FloatArgument())
        it.addArgument("z", FloatArgument())
        it.execute { ctx ->
            val player = ctx.playerOrThrow()
            val x = it.get<Float>("x")
            val y = it.get<Float>("y")
            val z = it.get<Float>("z")

            itemDisplay?.rotation?.value = Vector3f(x, y, z)
        }
    }

    val server = DockyardServer()
    server.start()
}