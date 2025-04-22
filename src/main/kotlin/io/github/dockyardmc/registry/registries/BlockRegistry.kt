package io.github.dockyardmc.registry.registries

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.registry.DataDrivenRegistry
import io.github.dockyardmc.registry.RegistryEntry
import io.github.dockyardmc.registry.RegistryException
import io.github.dockyardmc.utils.CustomDataHolder
import io.github.dockyardmc.world.block.Block
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPInputStream

@OptIn(ExperimentalSerializationApi::class)
object BlockRegistry: DataDrivenRegistry {

    val Air get() = BlockRegistry["minecraft:air"]

    override val identifier: String = "minecraft:block"

    var blocks: Object2ObjectOpenHashMap<String, RegistryBlock> = Object2ObjectOpenHashMap()
    var protocolIdToBlock: Int2ObjectOpenHashMap<RegistryBlock> = Int2ObjectOpenHashMap()
    var blockToProtocolId: Object2IntOpenHashMap<RegistryBlock> = Object2IntOpenHashMap()
    var protocolIdToBlockStates: Int2ObjectOpenHashMap<Block> = Int2ObjectOpenHashMap()

    val protocolIdCounter = AtomicInteger()

    override fun getMaxProtocolId(): Int {
        return protocolIdToBlock.keys.last()
    }

    override fun initialize(inputStream: InputStream) {
        val stream = GZIPInputStream(inputStream)
        val list = Json.decodeFromStream<List<RegistryBlock>>(stream)
        list.forEach { block ->
            val id = protocolIdCounter.getAndIncrement()
            protocolIdToBlock.put(block.defaultBlockStateId, block)
            blockToProtocolId[block] = id
            blocks[block.identifier] = block
        }

        blockToProtocolId.forEach { (block, _) ->
            if(block.states.isNotEmpty()) {
                block.possibleStates.forEach { (state, id) ->
                    val completeBlock = Block.getBlockFromStateString(state)
                    protocolIdToBlockStates[id] = completeBlock
                }
            }
        }
    }

    override fun get(identifier: String): RegistryBlock {
        return blocks[identifier] ?: throw RegistryException(identifier, getMap().size)
    }

    override fun getOrNull(identifier: String): RegistryBlock? {
        return blocks[identifier]
    }

    override fun getByProtocolId(id: Int): RegistryBlock {
        return protocolIdToBlock[id] ?: throw IllegalStateException("Block with protocol id $id is not in the registry!")
    }

    fun getByProtocolIdOrNull(id: Int): RegistryBlock? {
        return protocolIdToBlock[id]
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
    val blockEntityId: Int?,
    val lightFilter: Int,
    val isAir: Boolean,
    val isSolid: Boolean,
    val isLiquid: Boolean,
    val isFlammable: Boolean,
    val breakSpeed: Float,
    val requiresToolToBreak: Boolean,
    val canOcclude: Boolean,
    val replaceable: Boolean,
    val states: List<RegistryBlockState>,
    val defaultBlockStateId: Int,
    val minBlockStateId: Int,
    val maxBlockStateId: Int,
    val sounds: RegistryBlockSounds,
    val tags: List<String>,
    val possibleStates: Map<String, Int>,
    val shape: Map<Int, String>,
    val collisionShape: Map<Int, String>,
    val interactionShape: Map<Int, String>,
    val occlusionShape: Map<Int, String>,
    val visualShape: Map<Int, String>,
): RegistryEntry {

    @Contextual
    val possibleStatesReversed = Int2ObjectOpenHashMap(possibleStates.reversed())

    override fun getProtocolId(): Int {
        return defaultBlockStateId
    }

    fun getLegacyProtocolId(): Int {
        return BlockRegistry.blockToProtocolId[this] ?: throw RegistryException(identifier, BlockRegistry.blockToProtocolId.size)
    }

    fun toItem(): Item {
        return ItemRegistry[identifier]
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