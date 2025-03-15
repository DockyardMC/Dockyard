package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeItemStackList
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetContainerContentPacket(player: Player, items: List<ItemStack>) : ClientboundPacket() {

    init {
        buffer.writeVarInt(if(player.currentOpenInventory != null) 1 else 0)
        buffer.writeVarInt(0)
        buffer.writeItemStackList(items)
        player.inventory.cursorItem.value.write(buffer)
    }
}