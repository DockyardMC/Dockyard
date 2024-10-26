package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.UUID

@WikiVGEntry("Login Success")
@ClientboundPacketInfo(0x02, ProtocolState.LOGIN)
class ClientboundLoginSuccessPacket(uuid: UUID, username: String, properties: MutableList<ProfilePropertyMap>): ClientboundPacket() {
    init {
        data.writeUUID(uuid)
        data.writeUtf(username)
        data.writeVarInt(0)
//        properties.forEach(data::writeProfileProperties)
    }
}