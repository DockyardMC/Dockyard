package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf
import java.util.*

class ClientboundRemoveResourcepackPacket(uuid: UUID?) : ClientboundPacket() {

    init {
        buffer.writeOptional(uuid, ByteBuf::writeUUID)
    }
}