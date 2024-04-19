package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.UUID

class ClientboundLoginSuccessPacket(uuid: UUID, username: String): ClientboundPacket() {
    init {
        val size = 8 + username.byteSize() + 1 + ByteArray(0).size + 1
        data.writeVarInt(size)
        data.writeVarInt(2)

        data.writeUUID(uuid)
        data.writeUtf(username)
        data.writeInt(0)
        data.writeByteArray(ByteArray(0))
    }
}