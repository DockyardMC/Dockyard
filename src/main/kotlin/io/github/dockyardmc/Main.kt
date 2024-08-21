package io.github.dockyardmc

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.FloatArgument
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.StringArgument
import io.github.dockyardmc.datagen.EventsDocumentationGenerator
import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.entities.KinematicChain
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerPreSpawnWorldSelectionEvent
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.item.EnchantmentGlintOverrideItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.DustParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.*
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.runnables.AsyncQueueProcessor
import io.github.dockyardmc.runnables.AsyncQueueTask
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.utils.toVector3f
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
            val item = ItemStack(Items.IRON_SWORD)
            item.components.add(EnchantmentGlintOverrideItemComponent(true))
        }
    }

    var kinematic: KinematicChain? = null

    Commands.add("/spawn") {
        it.addArgument("segments", IntArgument())
        it.addArgument("scale", FloatArgument())
        it.execute { ctx ->
            val player = ctx.playerOrThrow()
            val segments = it.get<Int>("segments")
            val scale = it.get<Float>("scale")

            if(kinematic != null) {
                kinematic!!.dispose()
                kinematic = null
                player.sendMessage("<red>Destroyed previous kinematic chain!")
            }

            kinematic = KinematicChain(player.location, player.world, segments + 1, scale)
            player.inventory[0] = ItemStack(Items.SPECTRAL_ARROW, 1).apply { displayName.value = "<yellow><underline>pointy thingy"; hasGlint.value = true }
            player.sendMessage("<lime>Created new kinematic chain with <yellow>$segments segments<lime> of <yellow>$scale height<lime>!")
            player.playSound("minecraft:block.note_block.bit", player.location, 1f, 2f)
        }
    }

    Commands.add("/loc") {
        it.execute { ctx ->
            val player = ctx.playerOrThrow()
            kinematic?.fabrik(player.location)
        }
    }

    Commands.add("/rot") {
        it.addArgument("x", FloatArgument())
        it.addArgument("y", FloatArgument())
        it.addArgument("z", FloatArgument())
        it.execute { ctx ->
            val x = it.get<Float>("x")
            val y = it.get<Float>("y")
            val z = it.get<Float>("z")

            kinematic?.segments?.last()?.rotation?.value = Vector3f(x, y, z)
        }
    }

    val kinematicsThread = AsyncQueueProcessor()

    Events.on<ServerTickEvent> {
        if (kinematic == null) return@on
        val player = PlayerManager.players[0] ?: return@on
        if(player.getHeldItem(PlayerHand.MAIN_HAND).material == Items.SPECTRAL_ARROW) {
            val direction = player.getFacingDirectionVector()
            val targetLocation = player.location.add(direction.multiply(3.0)).add(0.0, 1.0, 0.0)
            kinematic?.fabrik(targetLocation)
        }

            var previousSegmentLocation: Location? = null
            kinematic!!.segments.forEach { segment ->
                val currentLocation = segment.location
                val particleFrequency = 10

                if (previousSegmentLocation != null) {
                    val difference = currentLocation.subtract(previousSegmentLocation!!)
                    val distance = difference.length()
                    val direction = difference.toVector3f().normalize()

                    val particleSpacing = 1.0 / particleFrequency
                    for (i in 0 until (distance / particleSpacing).toInt()) {
                        val particleLocation = previousSegmentLocation!!.add(direction.multiply(i * particleSpacing + particleSpacing / 2.0))
                        particleLocation.world.spawnParticle(
                            particleLocation,
                            Particles.BUBBLE,
                            speed = 0f,
                            count = 1,
                        )
                    }
                }

                previousSegmentLocation = currentLocation
            }
    }

    val server = DockyardServer()
    server.start()
}