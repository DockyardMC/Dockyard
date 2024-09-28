package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.log
import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.registry.Registry
import io.netty.buffer.ByteBuf

@WikiVGEntry("Registry Data")
@ClientboundPacketInfo(0x07, ProtocolState.CONFIGURATION)
class ClientboundRegistryDataPacket(val registry: Registry): ClientboundPacket() {

    init {
        log("writing registry ${registry::class.simpleName}")
        data.writeRegistry(registry)
    }
}

private fun ByteBuf.writeRegistry(registry: Registry) {
    this.writeUtf(registry.identifier)
    this.writeVarInt(registry.getMap().size)
    registry.getMap().forEach {
        log("Writing ${it.key}")
        val data = it.value.getNbt()
        val isDataPresent = it.value.getNbt() != null

        this.writeUtf(it.key)
        log("Data is present $isDataPresent")
        this.writeBoolean(isDataPresent)
        if(isDataPresent) {
            log("nbt: ${data!!.toSNBT()}")
            this.writeNBT(data)
        }
    }
}
