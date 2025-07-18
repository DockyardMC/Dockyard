package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket
import io.github.dockyardmc.protocol.types.writeList

class ClientboundSetContainerContentPacket(player: Player, items: List<ItemStack>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(if(player.currentlyOpenScreen != null) 1 else 0)
        buffer.writeVarInt(0)
        buffer.writeList(items, ItemStack::write)
        player.inventory.cursorItem.value.write(buffer)
    }
}