package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class SetItemCooldownPacket(var group: String, var cooldownTicks: Int): ClientboundPacket() {

    init {
        buffer.writeString(group)
        buffer.writeVarInt(cooldownTicks)
    }

}