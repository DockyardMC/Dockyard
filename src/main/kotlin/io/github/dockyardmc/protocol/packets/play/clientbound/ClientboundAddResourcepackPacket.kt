package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeOptionalOLD
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.Resourcepack

class ClientboundAddResourcepackPacket(resourcepack: Resourcepack) : ClientboundPacket() {

    init {
        buffer.writeUUID(resourcepack.uuid)
        buffer.writeString(resourcepack.url)
        buffer.writeString("what")
        buffer.writeBoolean(resourcepack.required)
        buffer.writeOptionalOLD(resourcepack.promptMessage) {
            it.writeTextComponent(resourcepack.promptMessage!!)
        }
    }
}