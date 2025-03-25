package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerCancelledDiggingEvent
import io.github.dockyardmc.events.PlayerFinishedDiggingEvent
import io.github.dockyardmc.events.PlayerStartDiggingBlockEvent
import io.github.dockyardmc.extentions.readByteEnum
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.item.ConsumableItemComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.hasType
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.maths.vectors.Vector3
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPlayerActionPacket(
    var action: PlayerAction,
    var position: Vector3,
    var face: Direction,
    var sequence: Int,
) : ServerboundPacket {

    companion object {

        private val cannotDig = mutableListOf(GameMode.ADVENTURE, GameMode.SPECTATOR)

        fun read(buf: ByteBuf): ServerboundPlayerActionPacket =
            ServerboundPlayerActionPacket(
                buf.readVarIntEnum<PlayerAction>(),
                buf.readBlockPosition(),
                buf.readByteEnum<Direction>(),
                buf.readVarInt()
            )
    }

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val location = position.toLocation(player.world)
        val block = player.world.getBlock(location)

        if (action == PlayerAction.CANCELLED_DIGGING) {
            if(cannotDig.contains(player.gameMode.value)) return

            Events.dispatch(PlayerCancelledDiggingEvent(player, location, block, getPlayerEventContext(player)))
            player.isDigging = false
        }

        if (action == PlayerAction.FINISHED_DIGGING) {
            if(cannotDig.contains(player.gameMode.value)) return

            Events.dispatch(PlayerFinishedDiggingEvent(player, location, block, getPlayerEventContext(player)))
            player.breakBlock(location, block, face)
        }

        if (action == PlayerAction.START_DIGGING) {
            if(cannotDig.contains(player.gameMode.value)) return

            Events.dispatch(PlayerStartDiggingBlockEvent(player, location, block, getPlayerEventContext(player)))
            player.isDigging = true

            if (player.gameMode.value != GameMode.CREATIVE) return
            player.breakBlock(location, block, face)
        }

        if (action == PlayerAction.HELD_ITEM_UPDATE) {

            val item = player.getHeldItem(PlayerHand.MAIN_HAND)
            val isConsumable = item.components.hasType(ConsumableItemComponent::class)
            if (isConsumable) {
                player.itemInUse = null
            }
        }

        if (action == PlayerAction.DROP_ITEM) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            if (held.isEmpty()) return
            val cancelled = player.inventory.drop(held.withAmount(1))
            if (cancelled) {
                player.inventory.sendFullInventoryUpdate()
                return
            }
            val newItem = if (held.amount - 1 == 0) ItemStack.AIR else held.withAmount(held.amount - 1)
            player.setHeldItem(PlayerHand.MAIN_HAND, newItem)
        }

        if (action == PlayerAction.DROP_ITEM_STACK) {
            val held = player.getHeldItem(PlayerHand.MAIN_HAND)
            if (held.isEmpty()) return
            val cancelled = player.inventory.drop(held)
            if (cancelled) {
                player.inventory.sendFullInventoryUpdate()
                return
            }
            player.setHeldItem(PlayerHand.MAIN_HAND, ItemStack.AIR)
        }

        if(action == PlayerAction.SWAP_ITEM) {
            player.inventory.swapOffhand()
        }
    }
}

enum class PlayerAction {
    START_DIGGING,
    CANCELLED_DIGGING,
    FINISHED_DIGGING,
    DROP_ITEM_STACK,
    DROP_ITEM,
    HELD_ITEM_UPDATE,
    SWAP_ITEM
}