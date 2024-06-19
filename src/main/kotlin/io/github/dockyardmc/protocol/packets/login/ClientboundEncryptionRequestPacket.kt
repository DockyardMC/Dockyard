package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Encryption Request")
@ClientboundPacketInfo(0x01, ProtocolState.LOGIN)
class ClientboundEncryptionRequestPacket(
    serverID: String,
    pubKey: ByteArray,
    verToken: ByteArray,
    shouldAuthenticate: Boolean,
): ClientboundPacket() {

    init {
        data.writeUtf(serverID)
        data.writeByteArray(pubKey)
        data.writeByteArray(verToken)
        data.writeBoolean(shouldAuthenticate)
    }
}