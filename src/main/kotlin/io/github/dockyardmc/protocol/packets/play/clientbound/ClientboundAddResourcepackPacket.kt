package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.writeOptional
import io.github.dockyardmc.resourcepack.Resourcepack
import io.netty.buffer.ByteBuf

class ClientboundAddResourcepackPacket(resourcepack: Resourcepack) : ClientboundPacket() {

    init {
        buffer.writeUUID(resourcepack.uuid)
        buffer.writeString(resourcepack.url)
        buffer.writeString("what")
        buffer.writeBoolean(resourcepack.required)
        buffer.writeOptional(resourcepack.promptMessage, ByteBuf::writeTextComponent)
    }
}