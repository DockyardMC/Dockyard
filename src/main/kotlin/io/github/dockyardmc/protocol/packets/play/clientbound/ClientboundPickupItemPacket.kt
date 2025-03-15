package io.github.dockyardmc.protocol.packets.play.clientbound

import io.github.dockyardmc.entity.Entity
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.ClientboundPacket

class ClientboundPickupItemPacket(val collected: ItemDropEntity, val collector: Entity, val item: ItemStack): ClientboundPacket() {

    init {
        buffer.writeVarInt(collected.id)
        buffer.writeVarInt(collector.id)
        buffer.writeVarInt(item.amount)
    }

}