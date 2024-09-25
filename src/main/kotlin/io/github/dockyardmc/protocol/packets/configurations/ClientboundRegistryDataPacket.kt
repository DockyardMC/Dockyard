package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.DynamicRegistry
import io.netty.buffer.ByteBuf

@WikiVGEntry("Registry Data")
@ClientboundPacketInfo(0x07, ProtocolState.CONFIGURATION)
class ClientboundRegistryDataPacket(registry: DynamicRegistry): ClientboundPacket() {

    init {
        data.writeRegistry(registry)
    }
}

private fun ByteBuf.writeRegistry(registry: DynamicRegistry) {
    this.writeUtf(registry.identifier)
    this.writeVarInt(registry.getMap().size)
    registry.getMap().forEach {
        val data = it.value.getNbt()
        val isDataPresent = it.value.getNbt() != null

        this.writeUtf(it.key)
        this.writeBoolean(isDataPresent)
        if(isDataPresent) this.writeNBT(data!!)
    }
}
