package io.github.dockyardmc.schematics

import io.github.dockyardmc.config.ConfigManager
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.getBlockFromStateString
import io.github.dockyardmc.utils.MathUtils
import io.github.dockyardmc.utils.vectors.Vector3
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray
import org.jglrxavpok.hephaistos.nbt.*
import java.io.File

object SchematicReader {

    val cache: MutableMap<String, Schematic> = mutableMapOf()

    fun read(file: File): Schematic {
        if(!file.exists()) throw Exception("File $file does not exist!")
        if(ConfigManager.currentConfig.serverConfig.cacheSchematics) {
            val hash = MathUtils.getFileHash(file, "SHA-256")
            val cachedSchematic = cache[hash]
            if(cachedSchematic != null) return cachedSchematic
        }

        val nbt = NBTReader(file, CompressedProcesser.GZIP).readNamed().second as NBTCompound

        val width: Int = (nbt.getShort("Width") ?: throw Exception("Field Width was not found in the schematic file!")).toInt()
        val height: Int = (nbt.getShort("Height") ?: throw Exception("Field Height was not found in the schematic file!")).toInt()
        val length: Int = (nbt.getShort("Length") ?: throw Exception("Field Length was not found in the schematic file!")).toInt()

        val metadata = nbt.getCompound("Metadata") ?: throw Exception("No metadata in schematic file!")

        var offset = Vector3()
        val version = nbt.getInt("Version")!!
        if(metadata.containsKey("WEOffsetX")) {
            offset = Vector3(
                x = metadata.getInt("WEOffsetX") ?: 0,
                y = metadata.getInt("WEOffsetY") ?: 0,
                z = metadata.getInt("WEOffsetZ") ?: 0
            )
        }

        val pallet: NBTCompound
        val blockArray: ImmutableByteArray
        if(version == 3) {
            val blockEntries = nbt.getCompound("Blocks") ?: throw Exception("No Blocks field in schematic file!")
            pallet = blockEntries.getCompound("Palette") ?: throw Exception("No Palette field in schematic file!")
            blockArray = blockEntries.getByteArray("Data") ?: throw Exception("No Data field in schematic file!")
        } else {
            pallet = nbt.getCompound("Palette") ?: throw Exception("No Palette field in schematic file!")
            blockArray = nbt.getByteArray("BlockData") ?: throw Exception("No Data field in schematic file!")
        }

        val blocks = mutableMapOf<Block, Int>()
        pallet.forEach { entry ->
            val namespace = entry.key.replace("minecraft:", "")
            val id = (entry.value as NBTInt).getValue()
            val block = getBlockFromStateString(namespace) ?: return@forEach
            blocks[block] = id
        }


        val schematic = Schematic(
            size = Vector3(width, height, length),
            offset = offset,
            pallete = blocks.toMutableMap(),
            blocks = blockArray.copyArray()
        )
        if(ConfigManager.currentConfig.serverConfig.cacheSchematics) {
            cache[MathUtils.getFileHash(file, "SHA-256")] = schematic
        }
        return schematic
    }
}

