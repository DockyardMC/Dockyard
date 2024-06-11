package io.github.dockyardmc.protocol.packets

import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

open class ClientboundPacket(val id: Int, val state: ProtocolState) {

    val data: ByteBuf = Unpooled.buffer()

    fun asByteBuf(): ByteBuf {
        val packetWithHeader = Unpooled.buffer()
        //┌──────────────────────────────────────┬───────────┬─────────────┐
        //│ Packet Size (data size + size of id) │ Packet Id │ Packet Data │
        //├──────────────────────────────────────┼───────────┼─────────────┤
        //│ VarInt                               │ VarInt    │ Bytes       │
        //└──────────────────────────────────────┴───────────┴─────────────┘
        packetWithHeader.writeVarInt(data.writerIndex() + 1)
        packetWithHeader.writeVarInt(id)
        packetWithHeader.writeBytes(data)
        return packetWithHeader
    }
}