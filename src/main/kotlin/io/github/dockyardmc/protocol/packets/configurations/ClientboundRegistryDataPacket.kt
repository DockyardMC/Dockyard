package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.Registry
import io.netty.buffer.ByteBuf

class ClientboundRegistryDataPacket(val registry: Registry): ClientboundPacket() {

    init {
        buffer.writeRegistry(registry)
    }
}

private fun ByteBuf.writeRegistry(registry: Registry) {
    this.writeString(registry.identifier)
    this.writeVarInt(registry.getMap().size)
    registry.getMap().forEach {
        val data = it.value.getNbt()
        val isDataPresent = it.value.getNbt() != null

        this.writeString(it.key)
        this.writeBoolean(isDataPresent)
        if(isDataPresent) {
            this.writeNBT(data!!)
        }
    }
}
