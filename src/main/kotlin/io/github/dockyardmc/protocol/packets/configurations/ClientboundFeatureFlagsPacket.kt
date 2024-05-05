package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.FeatureFlag
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundFeatureFlagsPacket(featureFlags: MutableList<FeatureFlag>): ClientboundPacket(8) {

    init {
        data.writeVarInt(featureFlags.size)
        featureFlags.forEach { data.writeUtf(it.identifier) }
    }
}