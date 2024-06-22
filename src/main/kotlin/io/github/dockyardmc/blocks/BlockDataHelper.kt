package io.github.dockyardmc.blocks

import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Blocks

//TODO Make this better this is pretty ugly, but idk where this info is actually stored in minecraft to make it data-driven
object BlockDataHelper {

    fun isClickable(block: Block): Boolean {
        if(block.namespace.contains("shulker_box")) return true
        if(block.namespace.contains("door")) return true
        return when(block) {
            Blocks.CHISELED_BOOKSHELF -> true
            Blocks.DECORATED_POT -> true
            Blocks.NOTE_BLOCK -> true
            Blocks.CHEST -> true
            Blocks.ENDER_CHEST -> true
            Blocks.TRAPPED_CHEST -> true
            Blocks.CRAFTING_TABLE -> true
            Blocks.CRAFTER -> true
            Blocks.FURNACE -> true
            Blocks.BLAST_FURNACE -> true
            Blocks.SMOKER -> true
            Blocks.ENCHANTING_TABLE -> true
            Blocks.ANVIL -> true
            Blocks.CHIPPED_ANVIL -> true
            Blocks.DAMAGED_ANVIL -> true
            Blocks.BREWING_STAND -> true
            Blocks.CAULDRON -> true
            Blocks.BELL -> true
            Blocks.COMPOSTER -> true
            Blocks.LOOM -> true
            Blocks.STONECUTTER -> true
            Blocks.GRINDSTONE -> true
            Blocks.BARREL -> true
            Blocks.SMITHING_TABLE -> true
            Blocks.FLETCHING_TABLE -> true
            Blocks.CARTOGRAPHY_TABLE -> true
            Blocks.LECTERN -> true
            Blocks.JUKEBOX -> true
            Blocks.BEACON -> true
            Blocks.BEEHIVE -> true
            Blocks.BEE_NEST -> true
            Blocks.DAYLIGHT_DETECTOR -> true
            Blocks.END_PORTAL_FRAME -> true
            Blocks.LEVER -> true
            Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE -> true
            Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE -> true
            Blocks.STONE_PRESSURE_PLATE -> true
            Blocks.STONE_BUTTON -> true
            Blocks.COMPARATOR -> true
            Blocks.REPEATER -> true
            Blocks.DAYLIGHT_DETECTOR -> true
            Blocks.TRAPPED_CHEST -> true
            Blocks.DISPENSER -> true
            Blocks.DROPPER -> true
            Blocks.HOPPER -> true
            Blocks.OAK_BUTTON -> true
            Blocks.SPRUCE_BUTTON -> true
            Blocks.BIRCH_BUTTON -> true
            Blocks.JUNGLE_BUTTON -> true
            Blocks.ACACIA_BUTTON -> true
            Blocks.DARK_OAK_BUTTON -> true
            Blocks.CRIMSON_BUTTON -> true
            Blocks.WARPED_BUTTON -> true
            Blocks.MANGROVE_BUTTON -> true
            Blocks.BAMBOO_BUTTON -> true
            Blocks.CHERRY_BUTTON -> true
            Blocks.OAK_DOOR -> true
            Blocks.SPRUCE_DOOR -> true
            Blocks.BIRCH_DOOR -> true
            Blocks.JUNGLE_DOOR -> true
            Blocks.ACACIA_DOOR -> true
            Blocks.DARK_OAK_DOOR -> true
            Blocks.CRIMSON_DOOR -> true
            Blocks.WARPED_DOOR -> true
            Blocks.MANGROVE_DOOR -> true
            Blocks.BAMBOO_DOOR -> true
            Blocks.CHERRY_DOOR -> true
            Blocks.OAK_TRAPDOOR -> true
            Blocks.SPRUCE_TRAPDOOR -> true
            Blocks.BIRCH_TRAPDOOR -> true
            Blocks.JUNGLE_TRAPDOOR -> true
            Blocks.ACACIA_TRAPDOOR -> true
            Blocks.DARK_OAK_TRAPDOOR -> true
            Blocks.CRIMSON_TRAPDOOR -> true
            Blocks.WARPED_TRAPDOOR -> true
            Blocks.MANGROVE_TRAPDOOR -> true
            Blocks.BAMBOO_TRAPDOOR -> true
            Blocks.CHERRY_TRAPDOOR -> true
            Blocks.OAK_FENCE_GATE -> true
            Blocks.SPRUCE_FENCE_GATE -> true
            Blocks.BIRCH_FENCE_GATE -> true
            Blocks.JUNGLE_FENCE_GATE -> true
            Blocks.ACACIA_FENCE_GATE -> true
            Blocks.DARK_OAK_FENCE_GATE -> true
            Blocks.CRIMSON_FENCE_GATE -> true
            Blocks.WARPED_FENCE_GATE -> true
            Blocks.MANGROVE_FENCE_GATE -> true
            Blocks.BAMBOO_FENCE_GATE -> true
            Blocks.CHERRY_FENCE_GATE -> true
            Blocks.COPPER_TRAPDOOR -> true
            Blocks.EXPOSED_COPPER_TRAPDOOR -> true
            Blocks.WEATHERED_COPPER_TRAPDOOR -> true
            Blocks.OXIDIZED_COPPER_TRAPDOOR -> true
            Blocks.WAXED_COPPER_TRAPDOOR -> true
            Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR -> true
            Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR -> true
            Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR -> true
            Blocks.COPPER_DOOR -> true
            Blocks.EXPOSED_COPPER_DOOR -> true
            Blocks.WEATHERED_COPPER_DOOR -> true
            Blocks.OXIDIZED_COPPER_DOOR -> true
            Blocks.WAXED_COPPER_DOOR -> true
            Blocks.WAXED_EXPOSED_COPPER_DOOR -> true
            Blocks.WAXED_WEATHERED_COPPER_DOOR -> true
            Blocks.WAXED_OXIDIZED_COPPER_DOOR -> true
            Blocks.TRIAL_SPAWNER -> true
            else -> false
        }
    }

}