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

    override fun toString(): String {
        return "ItemStack(${material.namespace}, $amount)"
    }
}


fun ByteBuf.readItemStack(): ItemStack {
    val count = this.readVarInt()
    if(count <= 0) return ItemStack.air

    return ItemStack(
        Items.getItemById(this.readVarInt()),
        count
    ) }


fun ByteBuf.writeItemStack(itemStack: ItemStack) {
    this.writeBoolean(true)
    this.writeVarInt(itemStack.material.id)
    this.writeByte(itemStack.amount)
}