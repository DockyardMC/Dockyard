@file:Suppress("ArrayInDataClass")

package io.github.dockyardmc.schematics

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.world.chunk.Chunk
import io.github.dockyardmc.world.World
import io.github.dockyardmc.world.block.Block
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import java.util.concurrent.CompletableFuture

data class Schematic(
    var size: Vector3,
    var offset: Vector3,
    var palette: Object2IntOpenHashMap<Block>,
    var blocks: ByteArray,
) {

    companion object {
        val empty = Schematic(Vector3(), Vector3(), Object2IntOpenHashMap(), ByteArray(0))
        val RED_STAINED_GLASS = Blocks.RED_STAINED_GLASS.toBlock()
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
    val updateChunks = ObjectOpenHashSet<Chunk>()
    val loadChunk = ObjectOpenHashSet<Pair<Int, Int>>()
    val batchBlockUpdate = ObjectArrayList<Pair<Location, Int>>()
    val flippedPallet = schematic.palette.reversed()

    for (y in 0 until schematic.size.y) {
        for (z in 0 until schematic.size.z) {
            for (x in 0 until schematic.size.x) {

                val placeLoc = Location(x, y, z, location.world).add(location)
                val id = blocks.readVarInt()
                val block = flippedPallet[id] ?: Schematic.RED_STAINED_GLASS

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
        chunk.sendUpdateToViewers()
    }
}