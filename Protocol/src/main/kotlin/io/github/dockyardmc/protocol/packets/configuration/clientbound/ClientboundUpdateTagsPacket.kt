package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.TagArray
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.writeList
import io.netty.buffer.ByteBuf

class ClientboundUpdateTagsPacket(val tags: List<TagArray>): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(tags, TagArray::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundUpdateTagsPacket {
            return ClientboundUpdateTagsPacket(buffer.readList(TagArray::read))
        }
    }

}