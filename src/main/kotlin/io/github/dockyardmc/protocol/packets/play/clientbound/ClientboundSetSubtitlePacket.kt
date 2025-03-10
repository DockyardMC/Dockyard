package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeNBT
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.scroll.Component

class ClientboundSetSubtitlePacket(component: Component) : ClientboundPacket() {
    init {
        data.writeNBT(component.toNBT())
    }
}