package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetInventoryCursorPacket(item: ItemStack): ClientboundPacket() {

    init {
        item.write(buffer)
    }

}