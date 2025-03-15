package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeOptionalOLD
import io.github.dockyardmc.extentions.writeUUID
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import java.util.*

class ClientboundRemoveResourcepackPacket(uuid: UUID?) : ClientboundPacket() {

    init {
        buffer.writeOptionalOLD(uuid) {
            buffer.writeUUID(uuid!!)
        }
    }
}