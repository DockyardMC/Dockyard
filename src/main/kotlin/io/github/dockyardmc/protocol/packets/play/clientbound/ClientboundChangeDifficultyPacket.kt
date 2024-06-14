package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.annotations.WikiVGEntry
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.packets.ProtocolState
import io.github.dockyardmc.world.Difficulty

@WikiVGEntry("Change Difficulty")
class ClientboundChangeDifficultyPacket(difficulty: Difficulty, locked: Boolean = false): ClientboundPacket(0x0B, ProtocolState.LOGIN) {

    init {
        data.writeByte(difficulty.ordinal)
        data.writeBoolean(locked)
    }
}