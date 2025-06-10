package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerPickItemFromBlockEvent
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.utils.getPlayerEventContext
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundPickItemFromBlockPacket(val blockPosition: Vector3, val includeData: Boolean): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player

        val location = blockPosition.toLocation(player.world)
        val block = location.block

        val event = PlayerPickItemFromBlockEvent(processor.player, location, block, includeData, getPlayerEventContext(player))
        Events.dispatch(event)
        if(event.cancelled) return
        if(event.block.isAir()) return

        val newItem = if(block.registryBlock == Blocks.REDSTONE_WIRE) Items.REDSTONE else block.toItem()

        if(player.gameMode.value == GameMode.CREATIVE) {
            player.mainHandItem = newItem.toItemStack()
        } else {
            val slot = player.inventory.getSlotByItem(newItem) ?: return

            if(slot <= 8) {
                player.heldSlotIndex.value = slot

            } else {
                val current = player.getHeldItem(PlayerHand.MAIN_HAND)
                val item = player.inventory[slot]

                player.setHeldItem(PlayerHand.MAIN_HAND, item)
                player.inventory[slot] = current
            }
        }
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundPickItemFromBlockPacket {
            return ServerboundPickItemFromBlockPacket(buffer.readBlockPosition(), buffer.readBoolean())
        }
    }
}