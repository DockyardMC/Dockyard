package io.github.dockyardmc.protocol.packets.configuration.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.ClientInformation
import io.netty.buffer.ByteBuf

class ServerboundClientInformationPacket(val clientInformation: ClientInformation) : Packet {

    override fun write(buffer: ByteBuf) {
        clientInformation.write(buffer)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundClientInformationPacket {
            return ServerboundClientInformationPacket(ClientInformation.read(buffer))
        }
    }

}