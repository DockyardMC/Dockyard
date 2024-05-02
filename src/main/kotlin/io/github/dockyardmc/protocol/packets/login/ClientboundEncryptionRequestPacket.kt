package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.netty.util.CharsetUtil

class ClientboundEncryptionRequestPacket(serverID: String, pubKey: ByteArray, verToken: ByteArray): ClientboundPacket(1) {

    init {
        data.writeUtf(serverID)
        data.writeByteArray(pubKey)
        data.writeByteArray(verToken)
    }
}