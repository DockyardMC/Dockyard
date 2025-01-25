package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Reset Score")
@ClientboundPacketInfo(0x44, ProtocolState.PLAY)
class ClientboundResetScorePacket(name: String, objective: String): ClientboundPacket() {

    init {
        data.writeString(name)
        data.writeBoolean(true)
        data.writeString(objective)
    }

}