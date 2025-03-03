package io.github.dockyardmc.world.block

import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.utils.CustomDataHolder

data class Block(
    val registryBlock: RegistryBlock,
    val blockStates: Map<String, String> = mutableMapOf(),
    val customData: CustomDataHolder? = null,
) {
    val identifier = registryBlock.identifier

    val tags = registryBlock.tags

    override fun equals(other: Any?): Boolean {
        if(other !is Block) return false
        return other.toString() == this.toString()
    }

    fun toItem(): Item {
        return ItemRegistry[identifier]
    }

    fun getProtocolId(): Int {
        if (blockStates.isEmpty()) return registryBlock.defaultBlockStateId
        if (registryBlock.states.isEmpty()) return registryBlock.defaultBlockStateId

        val id = registryBlock.possibleStates[this.toString()]
        return id ?: registryBlock.defaultBlockStateId
    }

    override fun toString(): String {
        if (registryBlock.states.isEmpty()) return identifier

        val baseBlockStatesString = registryBlock.possibleStatesReversed[registryBlock.defaultBlockStateId]!!
        val (_, baseStates) = io.github.dockyardmc.world.block.Block.Companion.parseBlockStateString(baseBlockStatesString)

        val states = mutableMapOf<String, String>()
        baseStates.forEach { states[it.key] = it.value }
        blockStates.forEach { states[it.key] = it.value }

        val stringBuilder = StringBuilder("$identifier[")
        states.entries.joinToString(separator = ",") { "${it.key}=${it.value}" }.also { stringBuilder.append(it) }

        return stringBuilder.append("]").toString()
    }

    fun withBlockStates(vararg states: Pair<String, String>): io.github.dockyardmc.world.block.Block {
        return withBlockStates(states.toMap())
    }

    fun withBlockStates(states: Map<String, String>): io.github.dockyardmc.world.block.Block {
        val newStates = blockStates.toMutableMap()
        newStates.putAll(states)
        return io.github.dockyardmc.world.block.Block(registryBlock, newStates, customData)
    }

    fun withCustomData(customDataHolder: CustomDataHolder): io.github.dockyardmc.world.block.Block {
        return io.github.dockyardmc.world.block.Block(registryBlock, blockStates, customDataHolder)
    }

    fun isAir(): Boolean {
        return this.registryBlock == Blocks.AIR
    }

    companion object {

        val AIR = io.github.dockyardmc.world.block.Block(BlockRegistry.Air)

        fun parseBlockStateString(string: String): Pair<String, Map<String, String>> {
            val index = string.indexOf('[')
            if (index == -1) return string to emptyMap()

            val block = string.substring(0, index)
            val statesPart = string.substring(index + 1, string.length - 1)

            val states = statesPart.split(',').associate {
                val (key, value) = it.split("=", limit = 2)
                key to value
            }

            return block to states
        }

        fun getBlockByStateId(stateId: Int): io.github.dockyardmc.world.block.Block {
            val registryBlock = BlockRegistry.getByProtocolIdOrNull(stateId)

            if (registryBlock != null) {
                val states = registryBlock.possibleStatesReversed[registryBlock.defaultBlockStateId]!!
                val parsed = io.github.dockyardmc.world.block.Block.Companion.parseBlockStateString(states).second.toMutableMap()
                return io.github.dockyardmc.world.block.Block(registryBlock, parsed)
            }

            for (block in BlockRegistry.protocolIdToBlock) {
                val cachedState = block.value.possibleStatesReversed
                if (cachedState.isEmpty()) continue
                if (!cachedState.containsKey(stateId)) continue

                val states = io.github.dockyardmc.world.block.Block.Companion.parseBlockStateString(cachedState[stateId]!!).second.toMutableMap()
                return io.github.dockyardmc.world.block.Block(block.value, states)
            }
            throw IllegalArgumentException("No block state found with $stateId")
        }

        fun getBlockFromStateString(identifier: String): io.github.dockyardmc.world.block.Block {
            val blockIdentifier = identifier.split("[")[0]
            val block = BlockRegistry[blockIdentifier]
            val id = block.possibleStates[identifier] ?: throw IllegalArgumentException("No matching state sequence found on ${block.identifier}")
            return io.github.dockyardmc.world.block.Block.Companion.getBlockByStateId(id)
        }
    }
}