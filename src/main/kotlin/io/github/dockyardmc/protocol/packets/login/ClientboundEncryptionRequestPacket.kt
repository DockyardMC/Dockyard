package io.github.dockyardmc.protocol.packets.login

import io.github.dockyardmc.extentions.writeByteArray
import io.github.dockyardmc.extentions.writeUtf
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.ktor.util.*
import io.netty.util.CharsetUtil
import log
import java.lang.Byte

class ClientboundEncryptionRequestPacket(serverID: String, pubKey: ByteArray, verToken: ByteArray): ClientboundPacket() {

    init {
        val size = pubKey.size + verToken.size * 2 +serverID.toByteArray(CharsetUtil.UTF_8).size + 1

        data.writeVarInt(size)
        data.writeVarInt(1)
        data.writeUtf(serverID)
        data.writeByteArray(pubKey)
        data.writeByteArray(verToken)
    }
}