package io.github.dockyardmc.plugins.bundled.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.ItemArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.MathUtils

class GiveCommand {
    init {
        Commands.add("/give") {
            it.permission = "dockyard.commands.give"
            it.addArgument("player", PlayerArgument())
            it.addArgument("item", ItemArgument())
            it.addOptionalArgument("amount", IntArgument())
            it.execute { ctx ->
                val player = it.get<Player>("player")
                val item = it.get<Item>("item")
                val amount = it.getOrNull<Int>("amount") ?: 1
                player.give(ItemStack(item, amount))
                player.playSound("minecraft:entity.item.pickup", pitch = MathUtils.randomFloat(0.8f, 1.3f))
            }
        }
    }
}