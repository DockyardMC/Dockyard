package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeOptional
import io.github.dockyardmc.extentions.writeTextComponent
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.resourcepack.Resourcepack

@WikiVGEntry("Add Resource Pack (play)")
@ClientboundPacketInfo(0x46, ProtocolState.PLAY)
class ClientboundAddResourcepackPacket(resourcepack: Resourcepack): ClientboundPacket() {

    init {
        data.writeUUID(resourcepack.uuid)
        data.writeString(resourcepack.url)
        data.writeString("what")
        data.writeBoolean(resourcepack.required)
        data.writeOptional(resourcepack.promptMessage) {
            it.writeTextComponent(resourcepack.promptMessage!!)
        }
    }
}