package io.github.dockyardmc.protocol.packets.login.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.writers.readString
import io.github.dockyardmc.protocol.writers.readUUID
import io.github.dockyardmc.protocol.writers.writeString
import io.github.dockyardmc.protocol.writers.writeUUID
import io.netty.buffer.ByteBuf
import java.util.UUID

class ServerboundLoginStartPacket(val name: String, val uuid: UUID): Packet {

    override fun write(buffer: ByteBuf) {
        buffer.writeString(name)
        buffer.writeUUID(uuid)
    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundLoginStartPacket {
            return ServerboundLoginStartPacket(
                buffer.readString(),
                buffer.readUUID()
            )
        }
    }

}