package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.*

class ClientboundLoginSuccessPacket(uuid: UUID, username: String, properties: MutableList<ProfilePropertyMap>): ClientboundPacket() {
    init {
        buffer.writeUUID(uuid)
        buffer.writeString(username)
        buffer.writeVarInt(0)
//        properties.forEach(data::writeProfileProperties)
    }
}