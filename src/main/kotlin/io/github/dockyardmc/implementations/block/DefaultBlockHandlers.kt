package io.github.dockyardmc.implementations.block

import io.github.dockyardmc.implementations.DefaultImplementationModule
import io.github.dockyardmc.world.block.DebugStick
import io.github.dockyardmc.world.block.handlers.*

class DefaultBlockHandlers: DefaultImplementationModule {

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

    override fun register() {
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:slabs", SlabBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:barrel", BarrelBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:buttons", ButtonBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:logs", LogBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:lantern", LanternBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:soul_lantern", LanternBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:soul_torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:redstone_torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:stairs", StairBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:trapdoors", TrapdoorBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:shulker_boxes", ShulkerboxBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:doors", DoorBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:fences", FenceBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:iron_bars", FenceBlockHandler())

        DebugStick().register()

        facingBlocks.forEach { block -> BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, block, FacingBlockHandler()) }
    }
}