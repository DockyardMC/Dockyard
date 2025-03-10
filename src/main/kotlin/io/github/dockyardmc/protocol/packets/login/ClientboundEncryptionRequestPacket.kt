package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundEncryptionRequestPacket(
    serverID: String,
    pubKey: ByteArray,
    verToken: ByteArray,
    shouldAuthenticate: Boolean,
): ClientboundPacket() {

    init {
        data.writeString(serverID)
        data.writeByteArray(pubKey)
        data.writeByteArray(verToken)
        data.writeBoolean(shouldAuthenticate)
    }
}