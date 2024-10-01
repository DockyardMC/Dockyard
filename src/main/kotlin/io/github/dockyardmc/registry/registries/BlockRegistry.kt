package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.utils.CustomDataHolder
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

    val Air = BlockRegistry["minecraft:air"]

    override val identifier: String = "minecraft:block"

    var blocks: Map<String, RegistryBlock> = mapOf()
    var protocolIdToBlock: Map<Int, RegistryBlock> = mapOf()

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<RegistryBlock>>(stream)
        blocks = list.associateBy { it.identifier }
        protocolIdToBlock = list.associateBy { it.defaultBlockStateId }
    }

    override fun get(identifier: String): RegistryBlock {
        return blocks[identifier] ?: throw IllegalStateException("Registry entry with identifier $identifier was not found")
    }

    override fun getOrNull(identifier: String): RegistryBlock? {
        return blocks[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryBlock {
        return protocolIdToBlock[id] ?: throw IllegalStateException("Block with protocol id $id is not in the registry!")
    }

    override fun getMap(): Map<String, RegistryBlock> {
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
    override fun getProtocolId(): Int {
        return defaultBlockStateId
    }

    fun toBlock(): Block {
        return Block(this)
    }

    fun withBlockStates(vararg states: Pair<String, String>): Block {
        return Block(this, states.toMap())
    }

    fun withBlockStates(states: Map<String, String>): Block {
        return Block(this, states.toMap())
    }

    fun withCustomData(customDataHolder: CustomDataHolder): Block {
        return Block(this, mutableMapOf(), customDataHolder)
    }

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