package io.github.dockyardmc.item

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.scroll.extensions.stripComponentTags
import io.github.dockyardmc.scroll.extensions.toComponent
import io.netty.buffer.ByteBuf

class ItemStack(var material: Item, var amount: Int = 1) {

    val components: BindableList<ItemComponent> = BindableList()
    val displayName: Bindable<String> = Bindable(material.displayName)
    val lore: BindableList<String> = BindableList()
    val customModelData: Bindable<Int> = Bindable(0)
    //TODO nice easy custom data api not like persistent containers or whatever the complicated fuck spigot uses
    val maxStackSize: Bindable<Int> = Bindable(64)
    val unbreakable: Bindable<Boolean> = Bindable(false)
    val hasGlint: Bindable<Boolean> = Bindable(false)

    init {
        displayName.valueChanged { components.addOrUpdate(CustomNameItemComponent(it.newValue.toComponent())) }
        lore.listUpdated { components.addOrUpdate(LoreItemComponent(lore.values.toComponents())) }
        customModelData.valueChanged { components.addOrUpdate(CustomModelDataItemComponent(it.newValue)) }
        maxStackSize.valueChanged { components.addOrUpdate(MaxStackSizeItemComponent(it.newValue)) }
        unbreakable.valueChanged { components.addOrUpdate(UnbreakableItemComponent(true)) }
        hasGlint.valueChanged { components.addOrUpdate(EnchantmentGlintOverrideItemComponent(it.newValue)) }
        if(amount <= 0) amount = 1
    }

    companion object {
        val air = ItemStack(Items.AIR, 0)
    }

    fun isEmpty(): Boolean = this.isSameAs(air)

    override fun toString(): String = "ItemStack(${material.identifier}, ${components.values}, $amount)".stripComponentTags()
}


fun ByteBuf.readItemStack(): ItemStack {
    val count = this.readVarInt()
    if(count <= 0) return ItemStack.air

    val itemId = this.readVarInt()
    val componentsToAdd = this.readVarInt()
    val componentsToRemove = this.readVarInt()

    val components: MutableList<ItemComponent> = mutableListOf()
    val removeComponents: MutableList<ItemComponent> = mutableListOf()

    for (i in 0 until componentsToAdd) {
        val type = this.readVarInt()
        val component = this.readComponent(type)
        components.add(component)
    }
    for (i in 0 until componentsToRemove) {
        val type = this.readVarInt()
//        removeComponents.add(ItemCom)
    }

    val item = ItemStack(ItemRegistry.getByProtocolId(itemId), count)
    components.forEach { item.components.add(it) }

    return item
}


fun ByteBuf.writeItemStack(itemStack: ItemStack) {
    if(itemStack.material == Items.AIR) {
        this.writeVarInt(0)
        return
    }

    this.writeVarInt(itemStack.amount)
    this.writeVarInt(itemStack.material.getProtocolId())
    this.writeVarInt(itemStack.components.size)
    this.writeVarInt(0)
    itemStack.components.values.forEach(this::writeItemComponent)
}

fun ItemStack.clone(): ItemStack {
    val itemStack = ItemStack(this.material, this.amount)
    itemStack.components.setValues(this.components.values.toMutableList())

    return itemStack
}

fun ItemStack.toComparisonString(): String = "ItemStack(${this.components.values};${this.material.identifier})".stripComponentTags()

fun ItemStack.isSameAs(other: ItemStack): Boolean = this.toComparisonString() == other.toComparisonString()