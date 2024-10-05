package io.github.dockyardmc.protocol.packets.configurations

import io.github.dockyardmc.server.FeatureFlag
import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Feature Flags")
@ClientboundPacketInfo(0x0C, ProtocolState.CONFIGURATION)
class ClientboundFeatureFlagsPacket(featureFlags: MutableList<FeatureFlag>): ClientboundPacket() {

    init {
        data.writeVarInt(featureFlags.size)
        featureFlags.forEach {
            data.writeUtf(it.identifier) }
    }
}