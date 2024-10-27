package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.events.ClientTickEndEvent
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundClientTickEndPacket: ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {
        Events.dispatch(ClientTickEndEvent(processor.player))
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundClientTickEndPacket {
            return ServerboundClientTickEndPacket()
        }
    }
}