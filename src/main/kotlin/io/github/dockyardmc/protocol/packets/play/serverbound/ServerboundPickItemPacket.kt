package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Pick Item")
@ServerboundPacketInfo(0x20, ProtocolState.PLAY)
class ServerboundPickItemPacket(var slot: Int): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        var newSlot = player.selectedHotbarSlot.value
        for (i in 1..9) {
            if (newSlot > 8) {newSlot = 0}
            if (player.inventory[newSlot] == ItemStack.air) {
                break
            }
            newSlot++
        }
        val oldSlot = slot
        val oldItem = player.inventory[newSlot]
        player.inventory[newSlot] = player.inventory[oldSlot]
        player.inventory[oldSlot] = oldItem
        player.selectedHotbarSlot.value = newSlot
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundPickItemPacket = ServerboundPickItemPacket(buf.readVarInt())
    }
}
