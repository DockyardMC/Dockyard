package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.registry.registries.DialogEntry

class ClientboundShowDialogPacket(dialog: DialogEntry) : ClientboundPacket() {
    init {
        buffer.writeVarInt(dialog.getProtocolId() + 1) // idk why +1 but its the only way it works
    }
}
