package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.extentions.writeItemStackList
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.writeItemStack
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundSetContainerContentPacket(player: Player, items: List<ItemStack>) : ClientboundPacket() {

    init {
        data.writeVarInt(if(player.currentOpenInventory != null) 1 else 0)
        data.writeVarInt(0)
        data.writeItemStackList(items)
        data.writeItemStack(player.inventory.cursorItem.value)
    }
}