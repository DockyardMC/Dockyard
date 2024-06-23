package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState

@WikiVGEntry("Commands")
@ClientboundPacketInfo(0x11, ProtocolState.PLAY)
class ClientboundCommandsPacket: ClientboundPacket() {

    init {
        //data is the buffer

    }
}