package io.github.dockyardmc

import io.github.dockyardmc.datagen.VerifyPacketIds
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.events.PlayerJoinEvent
import io.github.dockyardmc.events.PlayerPreSpawnWorldSelectionEvent
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.getBlockStateId
import io.github.dockyardmc.schematics.SchematicReader
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

    Events.on<PlayerJoinEvent> {
        val player = it.player
        player.gameMode.value = GameMode.CREATIVE
        player.inventory[0] = Items.OAK_SAPLING.toItemStack()
        player.inventory[1] = Items.DEBUG_STICK.toItemStack()
    }

    Events.on<PlayerBlockRightClickEvent> {
        val item = it.player.getHeldItem(PlayerHand.MAIN_HAND)
        val player = it.player
        if(item.material == Items.DEBUG_STICK && it.block == Blocks.OAK_SLAB) {
            val block = it.block

            val id = getBlockStateId(block, mapOf("type" to listOf("top", "bottom").random()), true)
            player.world.setBlockRaw(it.location, id)

//            when (block) {
//                Blocks.OAK_SAPLING -> it.location.world.setBlock(it.location, Blocks.OAK_SAPLING.withBlockState("stage" to "1"))
//                Blocks.OAK_STAIRS -> it.location.world.setBlock(it.location, Blocks.OAK_STAIRS.copy().apply { blockStates["waterlogged"] = "false" })
//                Blocks.OAK_LOG -> it.location.world.setBlock(it.location, Blocks.OAK_LOG.copy().apply { blockStates["axis"] = listOf("x", "y", "z").random() })
//                Blocks.OAK_SLAB -> it.location.world.setBlock(it.location, Blocks.OAK_SLAB.copy().apply { blockStates["type"] = listOf("bottom", "top").random() }) //what the fuck
//            }
        } else {
            player.sendMessage("${it.block.namespace} ${it.block.blockStates}")
        }
    }

    val server = DockyardServer()
    server.start()
}