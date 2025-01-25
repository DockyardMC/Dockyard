package io.github.dockyardmc.protocol.packets.login.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readByteArray
import io.github.dockyardmc.protocol.writers.writeByteArray
import io.netty.buffer.ByteBuf

class ServerboundEncryptionResponsePacket(val sharedSecret: ByteArray, val verifyToken: ByteArray): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeByteArray(sharedSecret)
        buffer.writeByteArray(verifyToken)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundEncryptionResponsePacket {
            return ServerboundEncryptionResponsePacket(buffer.readByteArray().clone(), buffer.readByteArray().clone())
        }
    }

}