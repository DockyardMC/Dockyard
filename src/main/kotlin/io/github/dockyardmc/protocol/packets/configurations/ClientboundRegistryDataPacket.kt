package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import org.jglrxavpok.hephaistos.nbt.NBT

class ClientboundRegistryDataPacket(compound: NBT): ClientboundPacket(5, ProtocolState.CONFIGURATION) {

    init {
        data.writeNBT(compound)
    }
}