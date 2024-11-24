package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.registry.ClientPacketRegistry
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

open class ClientboundPacket() {

    var id: Int? = null
    var state: ProtocolState? = null

    init {
        val packet = ClientPacketRegistry.getIdAndState(this::class)
        id = packet.first
        state = packet.second
    }

    val data: ByteBuf = Unpooled.buffer()

    fun asByteBuf(): ByteBuf {
        if (id == null) throw IllegalStateException("tried to send packet without id")
        if (state == null) throw IllegalStateException("tried to send packet without network state")

        val packetWithHeader = Unpooled.buffer()
        packetWithHeader.writeVarInt(data.copy().writerIndex() + 1)
        packetWithHeader.writeVarInt(id!!)
        packetWithHeader.writeBytes(data.copy())
        return packetWithHeader
    }
}