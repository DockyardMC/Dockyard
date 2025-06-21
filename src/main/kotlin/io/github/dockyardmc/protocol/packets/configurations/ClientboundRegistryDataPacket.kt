package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.Registry
import io.netty.buffer.ByteBuf

class ClientboundRegistryDataPacket(val registry: Registry<*>) : ClientboundPacket() {

    init {
        buffer.writeRegistry(registry)
    }
}

private fun ByteBuf.writeRegistry(registry: Registry<*>) {
    this.writeString(registry.identifier)
    this.writeVarInt(registry.getEntries().size)
    registry.getEntries().keyToValue().forEach { (identifier, entry) ->
        val data = entry.getNbt()
        val isDataPresent = data != null

        this.writeString(identifier)
        this.writeBoolean(isDataPresent)
        if (isDataPresent) {
            this.writeNBT(data!!)
        }
    }
}
