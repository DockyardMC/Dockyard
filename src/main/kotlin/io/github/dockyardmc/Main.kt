package io.github.dockyardmc

import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerPreSpawnWorldSelectionEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.particles.DustParticleData
import io.github.dockyardmc.particles.spawnParticle
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.add
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.DebugScoreboard
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.Vector3f
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.FlatWorldGenerator
import java.io.File

// This is just maya testing env.. do not actually run this
fun main(args: Array<String>) {

    if(args.contains("validate-packets")) {
        VerifyPacketIds()
        return
    }

    if(args.contains("schematic-test")) {
        val schem = File("./test.schem")
        SchematicReader.read(schem)
        return
    }

    val testWorld = WorldManager.create("test", FlatWorldGenerator(), DimensionTypes.OVERWORLD)
    testWorld.defaultSpawnLocation = Location(0, 201, 0, testWorld)

    Events.on<PlayerPreSpawnWorldSelectionEvent> {
        it.world = testWorld
    }

    Events.on<PlayerBlockRightClickEvent> {
        it.location.world.setBlockState(it.location, "snowy" to "true")
        it.player.playSound("minecraft:entity.chicken.egg", it.location, 1f, MathUtils.randomFloat(1.5f, 2f))
        it.location.world.spawnParticle(it.location.clone().centerBlockLocation(), Particles.DUST, count = 5, offset = Vector3f(0.7f), particleData = DustParticleData("#ffffff"))
    }

    Events.on<PlayerJoinEvent> {
        val player = it.player
        player.gameMode.value = GameMode.CREATIVE
        player.inventory[0] = Items.OAK_SAPLING.toItemStack()
        player.inventory[1] = Items.DEBUG_STICK.toItemStack()
        DebugScoreboard.sidebar.viewers.add(player)
    }

    val server = DockyardServer()
    server.start()
}