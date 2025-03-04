package io.github.dockyardmc.world.block.handlers

import io.github.dockyardmc.registry.registries.RegistryBlock

object BlockHandlerManager {

    private val blockHandlers: MutableMap<String, MutableList<BlockHandler>> = mutableMapOf()
    private val tagHandlers: MutableMap<String, MutableList<BlockHandler>> = mutableMapOf()

    fun register(type: Type, identifier: String, vararg handlers: BlockHandler) {
        register(type, identifier, handlers.toList())
    }

    fun register(type: Type, identifier: String, handlers: List<BlockHandler>) {
        val list = if(type == Type.TAG) tagHandlers else blockHandlers
        val entry = list[identifier] ?: mutableListOf()
        entry.addAll(handlers)

        list[identifier] = entry
    }

    fun getByTag(identifier: String): List<BlockHandler> {
        return tagHandlers[identifier] ?: emptyList()
    }

    fun getByBlock(identifier: String): List<BlockHandler> {
        return blockHandlers[identifier] ?: emptyList()
    }

    fun getAllFromRegistryBlock(block: RegistryBlock): List<BlockHandler> {
        val list = mutableListOf<BlockHandler>()
        block.tags.forEach { tag -> list.addAll(getByTag(tag)) }
        list.addAll(getByBlock(block.identifier))

        return list
    }

    enum class Type {
        BLOCK,
        TAG
    }
}