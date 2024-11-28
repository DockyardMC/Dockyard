package io.github.dockyardmc.blocks

import io.github.dockyardmc.extentions.reversed
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.RegistryBlock
import io.github.dockyardmc.utils.CustomDataHolder
import java.lang.IllegalArgumentException

data class Block(
    val registryBlock: RegistryBlock,
    val blockStates: Map<String, String> = mutableMapOf(),
    val customData: CustomDataHolder? = null,
) {
    val identifier = registryBlock.identifier

    fun getProtocolId(): Int {
        if (blockStates.isEmpty()) return registryBlock.defaultBlockStateId
        if (registryBlock.states.isEmpty()) return registryBlock.defaultBlockStateId

        val id = registryBlock.possibleStates[this.toString()]
        return id ?: registryBlock.defaultBlockStateId
    }

    override fun toString(): String {
        if (registryBlock.states.isEmpty()) return identifier

        val baseBlockStatesString = registryBlock.possibleStates.reversed()[registryBlock.defaultBlockStateId]!!
        val (_, baseStates) = parseBlockStateString(baseBlockStatesString)

        val states = mutableMapOf<String, String>()
        baseStates.forEach { states[it.key] = it.value }
        blockStates.forEach { states[it.key] = it.value }

        val stringBuilder = StringBuilder("$identifier[")
        states.entries.joinToString(separator = ",") { "${it.key}=${it.value}" }.also { stringBuilder.append(it) }

        return stringBuilder.append("]").toString()
    }

    fun withBlockStates(vararg states: Pair<String, String>): Block {
        return withBlockStates(states.toMap())
    }

    fun withBlockStates(states: Map<String, String>): Block {
        val newStates = blockStates.toMutableMap()
        newStates.putAll(states)
        return Block(registryBlock, newStates, customData)
    }

    fun withCustomData(customDataHolder: CustomDataHolder): Block {
        return Block(registryBlock, blockStates, customDataHolder)
    }

    fun isAir(): Boolean {
        return this.registryBlock == Blocks.AIR
    }

    companion object {

        val AIR = Block(BlockRegistry.Air)

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

        fun getBlockByStateId(stateId: Int): Block {
            val registryBlock = BlockRegistry.getByProtocolIdOrNull(stateId)

            if (registryBlock != null) {
                val states = registryBlock.possibleStates.reversed()[registryBlock.defaultBlockStateId]!!
                val parsed = parseBlockStateString(states).second.toMutableMap()
                return Block(registryBlock, parsed)
            }

            for (block in BlockRegistry.protocolIdToBlock) {
                val cachedState = block.value.possibleStates.reversed()
                if (cachedState.isEmpty()) continue
                if (!cachedState.containsKey(stateId)) continue

                val states = parseBlockStateString(cachedState[stateId]!!).second.toMutableMap()
                return Block(block.value, states)
            }
            throw IllegalArgumentException("No block state found with $stateId")
        }

        fun getBlockFromStateString(identifier: String): Block {
            val blockIdentifier = identifier.split("[")[0]
            val block = BlockRegistry[blockIdentifier]
            val id = block.possibleStates[identifier] ?: throw Exception("No matching state sequence found on ${block.identifier}")
            return getBlockByStateId(id)
        }
    }
}