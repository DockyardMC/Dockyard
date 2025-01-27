package io.github.dockyardmc.protocol.packets.play.serverbound

import io.github.dockyardmc.protocol.Packet
import io.github.dockyardmc.protocol.item.ItemStack
import io.netty.buffer.ByteBuf

class ServerboundContainerClickPacket(
    var windowId: Int,
    var stateId: Int,
    var slot: Int,
    var button: Int,
    var mode: ContainerClickMode,
    var changedSlots: MutableMap<Int, ItemStack>,
    var item: ItemStack,

    ): Packet {

    override fun write(buffer: ByteBuf) {

    }

    companion object {
        fun read(buffer: ByteBuf): ServerboundContainerClickPacket {
            return ServerboundContainerClickPacket()
        }
    }
}