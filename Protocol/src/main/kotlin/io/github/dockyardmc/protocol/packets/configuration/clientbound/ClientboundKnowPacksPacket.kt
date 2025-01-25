package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.KnownPack
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.writeList
import io.netty.buffer.ByteBuf

class ClientboundKnowPacksPacket(val knownPacks: List<KnownPack>): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(knownPacks, KnownPack::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundKnowPacksPacket {
            return ClientboundKnowPacksPacket(buffer.readList(KnownPack::read))
        }
    }
}