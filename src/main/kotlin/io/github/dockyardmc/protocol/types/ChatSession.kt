package io.github.dockyardmc.protocol.types

import io.github.dockyardmc.extentions.readUUID
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.NetworkReadable
import io.github.dockyardmc.protocol.NetworkWritable
import io.netty.buffer.ByteBuf
import java.util.*

data class ChatSession(val sessionId: UUID, val publicKey: PlayerPublicKey) : NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeUUID(sessionId)
        publicKey.write(buffer)
    }

    companion object : NetworkReadable<ChatSession> {
        override fun read(buffer: ByteBuf): ChatSession {
            return ChatSession(buffer.readUUID(), PlayerPublicKey.read(buffer))
        }
    }
}