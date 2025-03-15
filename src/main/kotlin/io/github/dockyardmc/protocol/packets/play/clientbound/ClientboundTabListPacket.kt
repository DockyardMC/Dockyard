package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundTabListPacket(
    header: Component,
    footer: Component
) : ClientboundPacket() {
    init {
        buffer.writeNBT(header.toNBT())
        buffer.writeNBT(footer.toNBT())
    }
}