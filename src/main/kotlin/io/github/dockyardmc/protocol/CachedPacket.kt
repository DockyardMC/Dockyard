package io.github.dockyardmc.protocol

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.netty.buffer.ByteBuf

data class CachedPacket(
    var id: Int,
    var data: ByteBuf,
    var state: ProtocolState
) {
    fun asClientboundPacket(): ClientboundPacket {
        val packet = ClientboundPacket()
        packet.data.writeBytes(data)
        packet.id = id
        packet.state = state
        return packet
    }

    companion object {
        fun fromClientboundPacket(clientboundPacket: ClientboundPacket): CachedPacket =
            CachedPacket(clientboundPacket.id!!, clientboundPacket.data, clientboundPacket.state!!)
    }
}