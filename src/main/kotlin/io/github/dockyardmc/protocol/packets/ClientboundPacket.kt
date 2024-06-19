package io.github.dockyardmc.protocol.packets

import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.AnnotationProcessor
import io.github.dockyardmc.extentions.writeVarInt
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.lang.Exception

open class ClientboundPacket() {

    var id: Int? = null
    var state: ProtocolState? = null

    init {
        if(AnnotationProcessor.clientboundPacketMap.containsKey(this::class.simpleName)) {
            val packet = AnnotationProcessor.clientboundPacketMap[this::class.simpleName]!!
            id = packet.first
            state = packet.second
        }
    }

    val data: ByteBuf = Unpooled.buffer()

    fun asByteBuf(): ByteBuf {
        if(id == null) throw Exception("tried to send packet without id")
        if(state == null) throw Exception("tried to send packet without network state")
        val packetWithHeader = Unpooled.buffer()
        //┌──────────────────────────────────────┬───────────┬─────────────┐
        //│ Packet Size (data size + size of id) │ Packet Id │ Packet Data │
        //├──────────────────────────────────────┼───────────┼─────────────┤
        //│ VarInt                               │ VarInt    │ Bytes       │
        //└──────────────────────────────────────┴───────────┴─────────────┘
        packetWithHeader.writeVarInt(data.writerIndex() + 1)
        packetWithHeader.writeVarInt(id!!)
        packetWithHeader.writeBytes(data)
        return packetWithHeader
    }
}