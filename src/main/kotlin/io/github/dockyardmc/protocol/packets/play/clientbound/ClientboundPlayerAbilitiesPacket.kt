package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.ClientboundPacket

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

        buffer.writeByte(mask)
        buffer.writeFloat(flyingSpeed)
        buffer.writeFloat(fovModifier)
    }
}