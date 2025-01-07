package io.github.dockyardmc.blocks

import io.github.dockyardmc.item.ToolItemComponent
import io.github.dockyardmc.item.getOrNull
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.registry.PotionEffects
import kotlin.math.ceil

object BlockBreakingCalculation {

    fun getBreakTicks(block: Block, player: Player): Int {
        if(player.gameMode.value == GameMode.CREATIVE) {
            return 0
        }

        // https://minecraft.wiki/w/Breaking#Calculation
        val registry = block.registryBlock
        val blockHardness = registry.breakSpeed
        if(blockHardness == -1f) {
            return -1
        }

        val item = player.mainHandItem

        val toolComponent: ToolItemComponent? = item.components.getOrNull(ToolItemComponent::class)
        val isBestTool = canBreakBlock(toolComponent, block)

        var speedMultiplier = 1f

        if(isBestTool) {
            speedMultiplier = getMiningSpeed(toolComponent, block)
            if(speedMultiplier > 1f) {
                speedMultiplier += 1f //TODO mining speed attribute
            }
        }

        if (player.potionEffects.contains(PotionEffects.HASTE)) {
            speedMultiplier *= getHasteMultiplier(player)
        }

        if (player.potionEffects.contains(PotionEffects.MINING_FATIGUE)) {
            speedMultiplier *= getMiningFatigueMultiplier(player)
        }

        //TODO block break speed attribute

        if(!player.isOnGround) {
            speedMultiplier /= 5
        }

        var damage = (speedMultiplier / blockHardness).toDouble()

        damage /= (if (isBestTool) 30.0 else 100.0)

        if (damage >= 1) return 0

        return ceil(1 / damage).toInt();
    }

    fun canBreakBlock(tool: ToolItemComponent?, block: Block): Boolean {
        return !block.registryBlock.requiresToolToBreak || isEffective(tool, block)
    }

    fun isEffective(tool: ToolItemComponent?, block: Block): Boolean {
        return tool != null && tool.isCorrectForDrops(block)
    }

    fun getHasteMultiplier(player: Player): Float {
        val effect = player.potionEffects[PotionEffects.HASTE]
        val level: Int = effect?.settings?.amplifier ?: 0
        return (1f + 0.2f * level)
    }

    fun getMiningFatigueMultiplier(player: Player): Float {
        val level: Int = player.potionEffects[PotionEffects.HASTE]?.settings?.amplifier ?: return 0f
        return when (level) {
            0 -> 0f
            1 -> 0.3f
            2 -> 0.09f
            3 -> 0.027f
            else -> 0.0081f
        }
    }

    fun getMiningSpeed(tool: ToolItemComponent?, block: Block): Float {
        if (tool == null) {
            return 1f
        }
        return tool.getSpeed(block)
    }

}


fun ToolItemComponent.isCorrectForDrops(block: Block): Boolean {
    toolRules.forEach { rule ->
        if(rule.speed != null && rule.blocks.contains(block))
            return rule.correctDropForBlocks
    }

    return false
}

fun ToolItemComponent.getSpeed(block: Block): Float {
    toolRules.forEach { rule ->
        if(rule.speed != null && rule.blocks.contains(block))
            return rule.speed
    }

    return defaultMiningSpeed
}