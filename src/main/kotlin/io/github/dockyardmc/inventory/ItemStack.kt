package io.github.dockyardmc.inventory

import io.github.dockyardmc.extentions.readNBT
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

class ItemStack(var material: Item, var amount: Int) {

    companion object {
        val air = ItemStack(Items.AIR, 1)
    }
}


data class TempItemStack(
    val present: Boolean,
    var itemId: Int? = null,
    var itemCount: Int? = null,
    var nbt: NBT? = null
)

fun ByteBuf.readItemStack(): TempItemStack {
    val present = this.readBoolean()
    val slotData = TempItemStack(present)
    if(present) {
        slotData.itemId = this.readVarInt()
        slotData.itemCount = this.readByte().toInt()
        slotData.nbt = this.readNBT()
    }
    return slotData
}


fun ByteBuf.writeItemStack(itemStack: ItemStack) {
    this.writeBoolean(true)
    this.writeVarInt(itemStack.material.id)
    this.writeByte(itemStack.amount)
}