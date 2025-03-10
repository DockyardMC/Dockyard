package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.world.Difficulty

class ClientboundChangeDifficultyPacket(difficulty: Difficulty, locked: Boolean = false) : ClientboundPacket() {

    init {
        data.writeByte(difficulty.ordinal)
        data.writeBoolean(locked)
    }
}