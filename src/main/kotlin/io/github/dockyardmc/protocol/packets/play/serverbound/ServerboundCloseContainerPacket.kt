package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.annotations.ServerboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

@WikiVGEntry("Close Container")
@ServerboundPacketInfo(0x0F, ProtocolState.PLAY)
class ServerboundCloseContainerPacket(val windowId: Int): ServerboundPacket {

    override fun handle(processor: PacketProcessor, connection: ChannelHandlerContext, size: Int, id: Int) {
        if(processor.player.currentOpenInventory.value != null) {
            val screen = processor.player.currentOpenInventory.value!!
            screen.closeListener?.invoke(processor.player)
            screen.dispose()
        }
        processor.player.currentOpenInventory.value = null
    }

    companion object {
        fun read(buf: ByteBuf): ServerboundCloseContainerPacket =
            ServerboundCloseContainerPacket(buf.readByte().toInt())
    }
}