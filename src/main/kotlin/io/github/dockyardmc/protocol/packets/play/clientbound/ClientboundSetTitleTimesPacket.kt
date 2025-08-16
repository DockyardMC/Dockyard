package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scheduler.runnables.inWholeMinecraftTicks
import kotlin.time.Duration

data class ClientboundSetTitleTimesPacket(
    val fadeIn: Duration,
    val stay: Duration,
    val fadeOut: Duration
) : ClientboundPacket() {
    init {
        buffer.writeInt(fadeIn.inWholeMinecraftTicks)
        buffer.writeInt(stay.inWholeMinecraftTicks)
        buffer.writeInt(fadeOut.inWholeMinecraftTicks)
    }
}