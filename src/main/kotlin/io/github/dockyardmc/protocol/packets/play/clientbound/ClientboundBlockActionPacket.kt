package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeByte
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.location.writeBlockPosition
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.RegistryBlock

class ClientboundBlockActionPacket(val location: Location, val blockAction: Byte, val actionParameter: Byte, val blockType: RegistryBlock): ClientboundPacket() {

    init {
        data.writeBlockPosition(location)
        data.writeByte(blockAction)
        data.writeByte(actionParameter)
        data.writeVarInt(blockType.getProtocolId())
    }
}