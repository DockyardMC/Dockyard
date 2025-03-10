package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeOptionalOLD
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.resourcepack.Resourcepack

class ClientboundAddResourcepackPacket(resourcepack: Resourcepack) : ClientboundPacket() {

    init {
        data.writeUUID(resourcepack.uuid)
        data.writeString(resourcepack.url)
        data.writeString("what")
        data.writeBoolean(resourcepack.required)
        data.writeOptionalOLD(resourcepack.promptMessage) {
            it.writeTextComponent(resourcepack.promptMessage!!)
        }
    }
}