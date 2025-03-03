package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.ClientboundPacketInfo
import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.extentions.writeOptionalOLD
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import java.util.*

@WikiVGEntry("Remove Resource Pack (play)")
@ClientboundPacketInfo(0x45, ProtocolState.PLAY)
class ClientboundRemoveResourcepackPacket(uuid: UUID?): ClientboundPacket() {

    init {
        data.writeOptionalOLD(uuid) {
            data.writeUUID(uuid!!)
        }
    }
}