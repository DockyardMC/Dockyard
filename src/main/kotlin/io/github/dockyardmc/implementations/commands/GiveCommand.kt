package io.github.dockyardmc.implementations.commands

import io.github.dockyardmc.commands.Commands
import io.github.dockyardmc.commands.IntArgument
import io.github.dockyardmc.commands.ItemArgument
import io.github.dockyardmc.commands.PlayerArgument
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.sounds.playSound
import io.github.dockyardmc.utils.randomFloat

class GiveCommand {
    init {
        Commands.add("/give") {
            withPermission("dockyard.commands.give")
            withDescription("Gives items to player")

            addArgument("player", PlayerArgument())
            addArgument("item", ItemArgument())
            addOptionalArgument("amount", IntArgument())
            execute {
                val player = getArgument<Player>("player")
                val item = getArgument<Item>("item")
                val amount = getArgumentOrNull<Int>("amount") ?: 1
                player.give(ItemStack(item, amount))
                player.playSound("minecraft:entity.item.pickup", pitch = randomFloat(0.8f, 1.3f))
            }
        }
    }
}