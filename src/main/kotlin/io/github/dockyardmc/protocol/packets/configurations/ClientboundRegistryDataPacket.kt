package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.registry.Registry
import io.netty.buffer.ByteBuf

class ClientboundRegistryDataPacket(val registry: Registry<*>) : ClientboundPacket() {

    init {
        buffer.writeRegistry(registry)
    }
}

private fun ByteBuf.writeRegistry(registry: Registry<*>) {
    val entries = registry.getProtocolEntries().keyToValue()
    val size = registry.getMaxProtocolId()

    this.writeString(registry.identifier)

    this.writeVarInt(size)
    for (i in 0..<size) {
        val entry = entries[i]!!

        writeString(entry.getEntryIdentifier())
        writeOptional(entry.getNbt(), ByteBuf::writeNBT)
    }
}
