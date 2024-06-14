package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Player Abilities (clientbound)")
class ClientboundPlayerAbilitiesPacket(
    isFlying: Boolean = false,
    isInvulnerable: Boolean = false,
    allowFlying: Boolean = false,
    flyingSpeed: Float = 0.05f,
    fovModifier: Float = 0.1f,
): ClientboundPacket(0x38, ProtocolState.PLAY) {

    init {
        var mask = 0
        if(isInvulnerable) mask += 1
        if(isFlying) mask += 2
        if(allowFlying) mask += 4

        data.writeByte(mask)
        data.writeFloat(flyingSpeed)
        data.writeFloat(fovModifier)
    }
}