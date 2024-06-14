package io.github.dockyardmc.protocol.packets.configurations

import cz.lukynka.prettylog.log
import io.github.dockyardmc.FeatureFlag
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Feature Flags")
class ClientboundFeatureFlagsPacket(featureFlags: MutableList<FeatureFlag>): ClientboundPacket(0x0C, ProtocolState.CONFIGURATION) {

    init {
        data.writeVarInt(featureFlags.size)
        featureFlags.forEach {
            data.writeUtf(it.identifier) }
    }
}