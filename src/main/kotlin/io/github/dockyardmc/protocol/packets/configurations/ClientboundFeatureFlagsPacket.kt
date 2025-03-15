package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.server.FeatureFlags

class ClientboundFeatureFlagsPacket(flags: MutableList<FeatureFlags.Flag>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(flags.size)
        flags.forEach {
            buffer.writeString(it.identifier)
        }
    }
}