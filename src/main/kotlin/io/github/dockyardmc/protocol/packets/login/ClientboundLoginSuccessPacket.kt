package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.*

@WikiVGEntry("Login Success")
@ClientboundPacketInfo(0x02, ProtocolState.LOGIN)
class ClientboundLoginSuccessPacket(uuid: UUID, username: String, properties: MutableList<ProfilePropertyMap>): ClientboundPacket() {
    init {
        data.writeUUID(uuid)
        data.writeString(username)
        data.writeVarInt(0)
//        properties.forEach(data::writeProfileProperties)
    }
}