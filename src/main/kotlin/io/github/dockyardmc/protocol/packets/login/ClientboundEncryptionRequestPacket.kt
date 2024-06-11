package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

class ClientboundEncryptionRequestPacket(serverID: String, pubKey: ByteArray, verToken: ByteArray): ClientboundPacket(1, ProtocolState.LOGIN) {

    init {
        data.writeUtf(serverID)
        data.writeByteArray(pubKey)
        data.writeByteArray(verToken)
    }
}