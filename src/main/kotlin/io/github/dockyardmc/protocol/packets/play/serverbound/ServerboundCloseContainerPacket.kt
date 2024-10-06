package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.ui.DrawableContainerScreen
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Close Container")
@ServerboundPacketInfo(0x0F, ProtocolState.PLAY)
class ServerboundCloseContainerPacket(val windowId: Int): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val openInventory = player.currentOpenInventory
        if(openInventory != null && openInventory is DrawableContainerScreen) {
            openInventory.onClose(player)
            openInventory.dispose()
        }
        processor.player.currentOpenInventory = null
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundCloseContainerPacket =
            ServerboundCloseContainerPacket(buf.readByte().toInt())
    }
}