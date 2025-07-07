package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.GameProfile
import java.util.*

class ClientboundLoginSuccessPacket(uuid: UUID, username: String, gameProfile: GameProfile) : ClientboundPacket() {
    init {
        gameProfile.write(buffer)
    }
}