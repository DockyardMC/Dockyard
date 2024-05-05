package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.protocol.packets.ServerboundPacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundClientInformationPacket
import io.github.dockyardmc.protocol.packets.configurations.ServerboundPluginMessagePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundEncryptionResponsePacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginStartPacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundHandshakePacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundPingRequestPacket
import io.github.dockyardmc.protocol.packets.handshake.ServerboundStatusRequestPacket
import io.github.dockyardmc.protocol.packets.login.ServerboundLoginAcknowledgedPacket
import io.netty.buffer.ByteBuf

object PacketParser {

    fun parsePacket(id: Int, buffer: ByteBuf, processor: PacketProcessor, size: Int): ServerboundPacket? {

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
                3 -> ServerboundLoginAcknowledgedPacket()
                else -> null
            }
        }

        if(processor.state == ProtocolState.CONFIGURATION) {
            outPacket = when(id) {
                0 -> ServerboundClientInformationPacket.read(buffer)
                1 -> ServerboundPluginMessagePacket.read(buffer)
                else -> null
            }
        }

        return outPacket
    }
}