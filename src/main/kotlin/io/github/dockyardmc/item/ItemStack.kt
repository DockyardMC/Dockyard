package io.github.dockyardmc.item

import io.github.dockyardmc.bindables.Bindable
import io.github.dockyardmc.bindables.BindableMutableList
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Item
import io.github.dockyardmc.registry.Items
import io.netty.buffer.ByteBuf

class ItemStack(var material: Item, var amount: Int = 1) {

    val components: BindableMutableList<ItemComponent> = BindableMutableList()
    val displayName: Bindable<String> = Bindable("")
    val lore: BindableMutableList<String> = BindableMutableList()
    val customModelData: Bindable<Int> = Bindable(0)
    //TODO nice easy custom data api not like persistent containers or whatever the complicated fuck spigot uses
    val maxStackSize: Bindable<Int> = Bindable(64)
    val unbreakable: Bindable<Boolean> = Bindable(false)

    init {
        displayName.valueChanged { components.addOrUpdate(CustomNameItemComponent(it.newValue)) }
        lore.listUpdated { components.addOrUpdate(LoreItemComponent(lore.values)) }
        customModelData.valueChanged { components.addOrUpdate(CustomModelDataItemComponent(it.newValue)) }
        maxStackSize.valueChanged { components.addOrUpdate(MaxStackSizeItemComponent(it.newValue)) }
        unbreakable.valueChanged { components.addOrUpdate(UnbreakableItemComponent(true)) }
    }

    companion object {
        val air = ItemStack(Items.AIR, 1)
    }

    override fun toString(): String = "ItemStack(${material.namespace}, $amount)"
}


fun ByteBuf.readItemStack(): ItemStack {
    val count = this.readVarInt()
    if(count <= 0) return ItemStack.air

    return ItemStack(
        Items.getItemById(this.readVarInt()),
        count
    ) }


fun ByteBuf.writeItemStack(itemStack: ItemStack) {
    if(itemStack.material == Items.AIR) {
        this.writeVarInt(0)
        return
    }

    this.writeVarInt(itemStack.amount)
    this.writeVarInt(itemStack.material.id)
    this.writeVarInt(itemStack.components.size)
    this.writeVarInt(0)
    itemStack.components.values.forEach(this::writeItemComponent)
}