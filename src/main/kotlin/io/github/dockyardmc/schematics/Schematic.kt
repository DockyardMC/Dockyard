@file:Suppress("ArrayInDataClass")

package io.github.dockyardmc.schematics

import io.github.dockyardmc.extentions.addIfNotPresent
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.sendPacket
import io.github.dockyardmc.extentions.toByteBuf
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.runnables.AsyncRunnable
import io.github.dockyardmc.utils.Vector3
import io.github.dockyardmc.world.Chunk
import io.github.dockyardmc.world.World

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

//TODO Implement rotation
//TODO implement block states
fun World.placeSchematic(location: Location, schematic: Schematic, rotation: SchematicRotation = SchematicRotation.NONE) {
    val blocks = schematic.blocks.toByteBuf()
    val updateChunks = mutableListOf<Chunk>()
    val runnable = AsyncRunnable {
        for (y in 0 until schematic.size.y) {
            for (z in 0 until schematic.size.z) {
                for (x in 0 until schematic.size.x) {
                    val placeLoc = Location(x, y, z, location.world).add(location)
                    val id = blocks.readVarInt()
                    val flippedPallet = schematic.pallete.entries.associateBy({ it.value }) { it.key }
                    val block = flippedPallet[id] ?: Blocks.RED_STAINED_GLASS

                    val chunk = placeLoc.getChunk()
                    if(chunk != null) updateChunks.addIfNotPresent(chunk)
                    this.setBlockRaw(placeLoc, block.getId(), false)
                }
            }
        }
    }
    runnable.callback = {
        updateChunks.forEach { chunk -> location.world.players.values.sendPacket(chunk.packet) }
    }
    runnable.run()
}

enum class SchematicRotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270,
}

