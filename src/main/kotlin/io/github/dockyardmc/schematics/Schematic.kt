@file:Suppress("ArrayInDataClass")

package io.github.dockyardmc.schematics

import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.getId
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.World
import java.lang.IllegalArgumentException

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

class SchematicPlacer {
    var schematic: Schematic? = null
    var location: Location? = null
    var rotation: SchematicRotation = SchematicRotation.NONE
    var then: (() -> Unit)? = null
}

fun World.placeSchematic(builder: SchematicPlacer.() -> Unit) {
    val placer = SchematicPlacer()
    builder.invoke(placer)

    val schematic = placer.schematic ?: throw IllegalArgumentException("You need to specify schematic")
    val location = placer.location ?: throw IllegalArgumentException("You need to specify location")
    val rotation = placer.rotation

    val blocks = schematic.blocks.toByteBuf()
    val updateChunks = mutableSetOf<Chunk>()
    val batchBlockUpdate = mutableListOf<Pair<Location, Int>>()

    val flippedPallet = schematic.pallete.entries.associateBy({ it.value }) { it.key }

    val runnable = AsyncRunnable {
        for (y in 0 until schematic.size.y) {
            for (z in 0 until schematic.size.z) {
                for (x in 0 until schematic.size.x) {
                    val placeLoc = Location(x, y, z, location.world).add(location)
                    val id = blocks.readVarInt()
                    val block = flippedPallet[id] ?: Blocks.RED_STAINED_GLASS

                    val chunk = placeLoc.getChunk()
                    if(chunk != null) updateChunks.add(chunk)
                    batchBlockUpdate.add(placeLoc to block.getId())
                }
            }
        }
        batchBlockUpdate.forEach { this.setBlockRaw(it.first, it.second, false) }
    }
    runnable.callback = {
        updateChunks.forEach { chunk -> location.world.players.values.sendPacket(chunk.packet) }
        placer.then?.invoke()
    }
    runnable.run()
}

enum class SchematicRotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270,
}

