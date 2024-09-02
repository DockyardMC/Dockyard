package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.player.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Player Abilities (clientbound)")
@ClientboundPacketInfo(0x38, ProtocolState.PLAY)
class ClientboundPlayerAbilitiesPacket(
    isFlying: Boolean = false,
    isInvulnerable: Boolean = false,
    allowFlying: Boolean = false,
    flyingSpeed: Float = 0.05f,
    fovModifier: Float = 0.1f,
    gamemode: GameMode = GameMode.SURVIVAL,
): ClientboundPacket() {

    init {
        var mask = 0
        if(isInvulnerable) mask += 1
        if(isFlying) mask += 2
        if(allowFlying) mask += 4
        if(gamemode == GameMode.CREATIVE) mask += 8

        data.writeByte(mask)
        data.writeFloat(flyingSpeed)
        data.writeFloat(fovModifier)
    }
}