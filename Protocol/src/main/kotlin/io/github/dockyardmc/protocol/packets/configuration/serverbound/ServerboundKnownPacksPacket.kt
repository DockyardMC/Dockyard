package io.github.dockyardmc.protocol.packets.configuration.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.KnownPack
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.writeList
import io.netty.buffer.ByteBuf

class ServerboundKnownPacksPacket(val knownPacks: List<KnownPack>): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(knownPacks, KnownPack::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundKnownPacksPacket {
            return ServerboundKnownPacksPacket(buffer.readList(KnownPack::read))
        }
    }
}