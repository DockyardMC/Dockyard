package io.github.dockyardmc.implementations.block

import io.github.dockyardmc.world.block.handlers.BlockHandlerManager
import io.github.dockyardmc.world.block.handlers.FacingBlockHandler
import io.github.dockyardmc.world.block.handlers.SlabHandler

class DefaultBlockHandlers {

    private val facingBlocks: List<String> = listOf(
        "minecraft:furnace",
        "minecraft:blast_furnace",
        "minecraft:smoker",
        "minecraft:chiseled_bookshelf",
        "minecraft:beehive",
        "minecraft:bee_nest",
        "minecraft:observer",
        "minecraft:end_portal_frame",
        "minecraft:campfire",
        "minecraft:chest",
        "minecraft:trapped_chest",
        "minecraft:bookshelf",
    )

    fun register() {
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:slabs", SlabHandler())
        facingBlocks.forEach { block -> BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, block, FacingBlockHandler()) }

    }
}