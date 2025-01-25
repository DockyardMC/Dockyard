package io.github.dockyardmc.protocol.packets.configuration.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.types.ServerLink
import io.github.dockyardmc.protocol.writers.readList
import io.github.dockyardmc.protocol.writers.writeList
import io.netty.buffer.ByteBuf

class ClientboundConfigurationServerLinksPacket(val links: Collection<ServerLink>): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeList(links, ServerLink::write)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundConfigurationServerLinksPacket {
            return ClientboundConfigurationServerLinksPacket(buffer.readList(ServerLink::read))
        }
    }

}