package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerHandshakeEvent
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.readVarIntEnum
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket

class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val nextState: ProtocolState,
): ServerboundPacket {

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {

        val event = ServerHandshakeEvent(version, serverAddress, port, nextState, Event.Context(isGlobalEvent = true))
        Events.dispatch(event)
        if(event.cancelled) return

        if(nextState == ProtocolState.LOGIN) {
            processor.loginHandler.handleHandshake(this, connection)
            return
        }

        processor.state = ProtocolState.STATUS
    }

    companion object {
        fun read(byteBuf: ByteBuf): ServerboundHandshakePacket {
            return ServerboundHandshakePacket(
                version = byteBuf.readVarInt(),
                serverAddress = byteBuf.readString(),
                port = byteBuf.readUnsignedShort().toShort(),
                nextState = byteBuf.readVarIntEnum<ProtocolState>()
            )
        }
    }
}