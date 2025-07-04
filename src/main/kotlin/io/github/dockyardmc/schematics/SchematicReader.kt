package io.github.dockyardmc.schematics

import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.scroll.extensions.contains
import io.github.dockyardmc.world.block.Block
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.nbt.BinaryTagIO
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import java.io.ByteArrayInputStream
import java.io.File

object SchematicReader {

    val READER = BinaryTagIO.unlimitedReader()

    fun read(file: File): Schematic {
        if (!file.exists()) throw Exception("File $file does not exist!")
        return read(file.readBytes())
    }

    fun read(byteArray: ByteArray): Schematic {

        var nbt = READER.read(ByteArrayInputStream(byteArray), BinaryTagIO.Compression.GZIP)

        // newer versions of FAWE put it in there for some reason?
        if (nbt.contains("Schematic")) {
            nbt = nbt.getCompound("Schematic")
        }

        val width: Int = (nbt.getShort("Width")).toInt()
        val height: Int = (nbt.getShort("Height")).toInt()
        val length: Int = (nbt.getShort("Length")).toInt()

        val metadata = nbt.getCompound("Metadata")

        var offset = Vector3()
        val version = nbt.getInt("Version")
        if (metadata.contains("WEOffsetX")) {
            offset = Vector3(
                x = metadata.getInt("WEOffsetX"),
                y = metadata.getInt("WEOffsetY"),
                z = metadata.getInt("WEOffsetZ")
            )
        }

        val pallet: CompoundBinaryTag
        val blockArray: ByteArray
        if (version == 3) {
            val blockEntries = nbt.getCompound("Blocks")
            pallet = blockEntries.getCompound("Palette")
            blockArray = blockEntries.getByteArray("Data")
        } else {
            pallet = nbt.getCompound("Palette")
            blockArray = nbt.getByteArray("BlockData")
        }

        val blocks = Object2IntOpenHashMap<Block>()
        pallet.forEach { entry ->
            val id = (entry.value as IntBinaryTag).value()
            val block = Block.getBlockFromStateString(entry.key)
            blocks[block] = id
        }

        val schematic = Schematic(
            size = Vector3(width, height, length),
            offset = offset,
            palette = blocks,
            blocks = blockArray.copyOf()
        )

        return schematic
    }
}