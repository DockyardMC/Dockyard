package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.utils.debug
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object BlockRegistry: DataDrivenRegistry {

    override val identifier: String = "minecraft:block"

    var blocks: Map<String, RegistryEntry> = mapOf()
    var protocolIdToBlock: Map<Int, RegistryEntry> = mapOf()

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<RegistryBlock>>(stream)
        blocks = list.associateBy { it.identifier }
        protocolIdToBlock = list.associateBy { it.defaultBlockStateId }
        debug("Loaded entity type registry: ${blocks.size} entries", false)
    }

    override fun get(identifier: String): RegistryEntry {
        return blocks[identifier] ?: throw IllegalStateException("Registry entry with identifier $identifier was not found")
    }

    override fun getOrNull(identifier: String): RegistryEntry? {
        return blocks[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryEntry {
        return protocolIdToBlock[id] ?: throw IllegalStateException("Block with protocol id $id is not in the registry!")
    }

    override fun getMap(): Map<String, RegistryEntry> {
        return blocks
    }
}

@Serializable
data class RegistryBlock(
    val identifier: String,
    val displayName: String,
    val explosionResistance: Float,
    val destroyTime: Float,
    val isSignalSource: Boolean,
    val lightEmission: Int,
    val isBlockEntity: Boolean,
    val lightFilter: Int,
    val isAir: Boolean,
    val isSolid: Boolean,
    val isLiquid: Boolean,
    val isFlammable: Boolean,
    val canOcclude: Boolean,
    val replaceable: Boolean,
    val states: List<RegistryBlockState>,
    val defaultBlockStateId: Int,
    val minBlockStateId: Int,
    val maxBlockStateId: Int,
    val sounds: RegistryBlockSounds,
    val tags: List<String>,
    val possibleStates: Map<String, Int>,
): RegistryEntry {
    override val protocolId: Int = defaultBlockStateId

    override fun getNbt(): NBTCompound? = null
}

@Serializable
data class RegistryBlockSounds(
    val breakSound: String,
    val hitSound: String,
    val placeSound: String,
    val fallSound: String,
    val walkSound: String,
)

@Serializable
data class RegistryBlockState(
    val name: String,
    val type: String,
    val values: List<String>? = null,
)