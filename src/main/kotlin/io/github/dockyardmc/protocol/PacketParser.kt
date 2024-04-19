package io.github.dockyardmc.protocol

import io.github.dockyardmc.PacketProcessor
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.login.ServerboundEncryptionResponsePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginStartPacket
import io.github.dockyardmc.protocol.packets.status.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.status.ServerboundPingRequestPacket
import io.github.dockyardmc.protocol.packets.status.ServerboundStatusRequestPacket
import io.netty.buffer.ByteBuf

object PacketParser {

    fun parsePacket(id: Int, buffer: ByteBuf, processor: PacketProcessor): ServerboundPacket? {

        var outPacket: ServerboundPacket? = null

        if(processor.state == ProtocolState.HANDSHAKE) {
            outPacket = when (id) {
                0 -> ServerboundHandshakePacket.read(buffer)
                else -> null
            }
        }

        if(processor.state == ProtocolState.STATUS) {
            outPacket = when (id) {
                0 -> ServerboundStatusRequestPacket.read(buffer)
                1 -> ServerboundPingRequestPacket.read(buffer)
                else -> null
            }
        }

        if(processor.state == ProtocolState.LOGIN) {
            outPacket = when(id) {
                0 -> ServerboundLoginStartPacket.read(buffer)
                1 -> ServerboundEncryptionResponsePacket.read(buffer)
                else -> null
            }
        }
        return outPacket
    }
}