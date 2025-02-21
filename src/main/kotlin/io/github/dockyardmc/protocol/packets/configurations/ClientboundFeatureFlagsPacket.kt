package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.server.FeatureFlags

class ClientboundFeatureFlagsPacket(flags: MutableList<FeatureFlags.Flag>) : ClientboundPacket() {

    init {
        data.writeVarInt(flags.size)
        flags.forEach {
            data.writeString(it.identifier)
        }
    }
}