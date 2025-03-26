package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.netty.buffer.ByteBuf

class ClientboundSelectAdvancementsTabPacket(val identifier: String?) : ClientboundPacket() {

    init {
        buffer.writeOptional(identifier, ByteBuf::writeString)
    }

}