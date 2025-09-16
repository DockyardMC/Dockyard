package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeString
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import kotlin.time.Duration

data class SetItemCooldownPacket(var group: String, var cooldown: Duration): ClientboundPacket() {

    init {
        buffer.writeString(group)
        buffer.writeVarInt(cooldown.inWholeMinecraftTicks)
    }

}