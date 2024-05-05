package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.UUID

class ClientboundLoginSuccessPacket(uuid: UUID, username: String, properties: MutableList<ProfilePropertyMap>): ClientboundPacket(2) {
    init {
        data.writeUUID(uuid)
        data.writeUtf(username)
        data.writeVarInt(0)

//        properties.forEach(data::writeProfileProperties)
    }
}