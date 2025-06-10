package io.github.dockyardmc.implementations.block

import io.github.dockyardmc.implementations.DefaultImplementationModule
import io.github.dockyardmc.world.block.DebugStick
import io.github.dockyardmc.world.block.handlers.*

class DefaultBlockHandlers : DefaultImplementationModule {

    private val facingBlocks: List<String> = listOf(
        "minecraft:furnace",
        "minecraft:blast_furnace",
        "minecraft:smoker",
        "minecraft:chiseled_bookshelf",
        "minecraft:beehive",
        "minecraft:bee_nest",
        "minecraft:end_portal_frame",
        "minecraft:campfire",
        "minecraft:chest",
        "minecraft:ender_chest",
        "minecraft:trapped_chest",
        "minecraft:bookshelf",
        "minecraft:lectern",
        "minecraft:loom",
        "minecraft:decorated_pot",
        "minecraft:ladder",
        "minecraft:vault",
        "minecraft:piston"
    )

    override fun register() {
        val fenceBlockHandler = FenceBlockHandler()
        val doublePlantHandler = DoublePlantBlockHandler()
        val axisFacingBlockHandler = AxisFacingBlockHandler()
        val facingBlockHandler = FacingBlockHandler(upDown = false)
        val facing6BlockHandler = FacingBlockHandler(upDown = true)
        val buttonBlockHandler = ButtonBlockHandler()

        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:slabs", SlabBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:buttons", buttonBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:grindstone", buttonBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:lever", buttonBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:lantern", LanternBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:soul_lantern", LanternBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:soul_torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:redstone_torch", TorchBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:stairs", StairBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:trapdoors", TrapdoorBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:doors", DoorBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:fences", fenceBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:iron_bars", fenceBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:snow", SnowLayerBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:candles", CandleBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:grass_block", GrassBlockBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:dirt", DirtBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:dirt_path", DirtPathBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:chiseled_bookshelf", ChiseledBookshelfBlockHandler())
        BlockHandlerManager.register(BlockHandlerManager.Type.TAG, "minecraft:fence_gates", facingBlockHandler)
        BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, "minecraft:redstone_wire", RedstoneDustHandler())

        DoublePlantBlockHandler.doublePlants.forEach { block ->
            BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, block.identifier, doublePlantHandler)
        }

        // ************ facing 6 ways ************
        listOf(
            "minecraft:barrel",
            "minecraft:end_rod",
            "minecraft:dispenser",
            "minecraft:dropper",
            "minecraft:observer",
            "minecraft:small_amethyst_bud",
            "minecraft:medium_amethyst_bud",
            "minecraft:large_amethyst_bud",
            "minecraft:amethyst_cluster",

            // thats funny one. if its facing up its invalid so it faces down as default
            "minecraft:hopper",
        ).forEach { id ->
            BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, id, facing6BlockHandler)
        }
        // tags
        listOf(
            "minecraft:shulker_boxes",
        ).forEach { id ->
            BlockHandlerManager.register(BlockHandlerManager.Type.TAG, id, facing6BlockHandler)
        }
        // ************ facing 6 ways ************

        listOf(
            "minecraft:glass_pane",
            "minecraft:white_stained_glass_pane",
            "minecraft:orange_stained_glass_pane",
            "minecraft:magenta_stained_glass_pane",
            "minecraft:light_blue_stained_glass_pane",
            "minecraft:yellow_stained_glass_pane",
            "minecraft:lime_stained_glass_pane",
            "minecraft:pink_stained_glass_pane",
            "minecraft:gray_stained_glass_pane",
            "minecraft:light_gray_stained_glass_pane",
            "minecraft:cyan_stained_glass_pane",
            "minecraft:purple_stained_glass_pane",
            "minecraft:blue_stained_glass_pane",
            "minecraft:brown_stained_glass_pane",
            "minecraft:green_stained_glass_pane",
            "minecraft:red_stained_glass_pane",
            "minecraft:black_stained_glass_pane",
        ).forEach { id ->
            BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, id, fenceBlockHandler)
        }

        listOf(
            "minecraft:pearlescent_froglight",
            "minecraft:verdant_froglight",
            "minecraft:ochre_froglight",
            "minecraft:chain",
            "minecraft:logs",
        ).forEach { id ->
            BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, id, axisFacingBlockHandler)
        }

        DebugStick().register()

        facingBlocks.forEach { block -> BlockHandlerManager.register(BlockHandlerManager.Type.BLOCK, block, facingBlockHandler) }
    }
}
