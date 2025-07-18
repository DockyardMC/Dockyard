package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundCloseContainerPacket(val windowId: Int) : ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        val player = processor.player
        val openInventory = player.currentlyOpenScreen
        if (openInventory != null) {
            openInventory.onClose()
            openInventory.dispose()
        }
        processor.player.currentlyOpenScreen = null
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundCloseContainerPacket =
            ServerboundCloseContainerPacket(buf.readByte().toInt())
    }
}