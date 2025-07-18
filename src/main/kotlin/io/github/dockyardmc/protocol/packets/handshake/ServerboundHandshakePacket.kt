package io.github.dockyardmc.protocol.packets.handshake

import io.github.dockyardmc.events.Event
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.ServerHandshakeEvent
import io.github.dockyardmc.extentions.readString
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

class ServerboundHandshakePacket(
    val version: Int,
    val serverAddress: String,
    val port: Short,
    val intent: Intent,
) : ServerboundPacket {

    enum class Intent(val id: Int) {
        STATUS(1),
        LOGIN(2),
        TRANSFER(3);

        companion object {
            fun fromId(id: Int): Intent {
                return entries.firstOrNull { intent -> intent.id == id } ?: throw IllegalArgumentException("Unknown connection intent")
            }
        }
    }

    override fun handle(processor: PlayerNetworkManager, connection: ChannelHandlerContext, size: Int, id: Int) {

        val event = ServerHandshakeEvent(version, serverAddress, port, intent, Event.Context(isGlobalEvent = true))
        Events.dispatch(event)
        if (event.cancelled) return

        processor.joinedThroughIp = serverAddress

        if (intent == Intent.LOGIN) {
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
                intent = Intent.fromId(byteBuf.readVarInt())
            )
        }
    }
}