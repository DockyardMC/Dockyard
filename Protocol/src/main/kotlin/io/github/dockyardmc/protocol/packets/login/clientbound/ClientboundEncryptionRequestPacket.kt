package io.github.dockyardmc.protocol.packets.login.clientbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readByteArray
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.writeByteArray
import io.github.dockyardmc.protocol.writers.writeString
import io.netty.buffer.ByteBuf

class ClientboundEncryptionRequestPacket(
    val serverID: String,
    val pubKey: ByteArray,
    val verToken: ByteArray,
    val shouldAuthenticate: Boolean,
): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(serverID)
        buffer.writeByteArray(pubKey)
        buffer.writeByteArray(verToken)
        buffer.writeBoolean(shouldAuthenticate)
    }

    companion object {
        fun read(buffer: ByteBuf): ClientboundEncryptionRequestPacket {
            return ClientboundEncryptionRequestPacket(
                buffer.readString(),
                buffer.readByteArray(),
                buffer.readByteArray(),
                buffer.readBoolean()
            )
        }
    }
}