@file:Suppress("ArrayInDataClass")

package io.github.dockyardmc.schematics

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.World
import java.util.concurrent.CompletableFuture

data class Schematic(
    var size: Vector3,
    var offset: Vector3,
    var pallete: MutableMap<Block, Int>,
    var blocks: ByteArray,
) {

    companion object {
        val empty = Schematic(Vector3(), Vector3(), mutableMapOf(), ByteArray(0))
    }
}

fun World.placeSchematicAsync(schematic: Schematic, location: Location): CompletableFuture<Unit> {
    return location.world.scheduler.runAsync {
        location.world.placeSchematic(schematic, location)
    }
}

fun World.placeSchematic(
    schematic: Schematic,
    location: Location,
) {
    val blocks = schematic.blocks.toByteBuf()
    val updateChunks = mutableSetOf<Chunk>()
    val loadChunk = mutableSetOf<Pair<Int, Int>>()
    val batchBlockUpdate = mutableListOf<Pair<Location, Int>>()

    val flippedPallet = schematic.pallete.entries.associateBy({ it.value }) { it.key }

    for (y in 0 until schematic.size.y) {
        for (z in 0 until schematic.size.z) {
            for (x in 0 until schematic.size.x) {

                val placeLoc = Location(x, y, z, location.world).add(location)
                val id = blocks.readVarInt()
                val block = flippedPallet[id] ?: Blocks.RED_STAINED_GLASS.toBlock()

                val chunkX = ChunkUtils.getChunkCoordinate(placeLoc.x)
                val chunkZ = ChunkUtils.getChunkCoordinate(placeLoc.z)

                loadChunk.add(chunkX to chunkZ)

                val chunk = placeLoc.world.getOrGenerateChunk(
                    ChunkUtils.getChunkCoordinate(placeLoc.x),
                    ChunkUtils.getChunkCoordinate(placeLoc.z)
                )
                updateChunks.add(chunk)
                batchBlockUpdate.add(placeLoc to block.getProtocolId())
            }
        }
    }

    batchBlockUpdate.forEach {
        try {
            this.setBlockRaw(it.first, it.second, false)
        } catch (ex: Exception) {
            log("Error while placing block in schematic at ${it.first}: $ex", LogType.ERROR)
            log(ex)
        }
    }

    updateChunks.forEach { chunk ->
        chunk.updateCache()
        location.world.players.sendPacket(chunk.packet)
    }
}