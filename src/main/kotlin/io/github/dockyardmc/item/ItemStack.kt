package io.github.dockyardmc.item

import cz.lukynka.Bindable
import cz.lukynka.BindableList
import io.github.dockyardmc.extentions.put
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.extensions.put
import io.github.dockyardmc.scroll.extensions.stripComponentTags
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.utils.CustomDataHolder
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.*
import java.io.UnsupportedEncodingException

@Suppress("UNCHECKED_CAST")
class ItemStack(var material: Item, var amount: Int = 1) {

    val components: BindableList<ItemComponent> = BindableList()
    val displayName: Bindable<String> = Bindable(material.displayName)
    val lore: BindableList<String> = BindableList()
    val customModelData: Bindable<Int> = Bindable(0)
    //TODO nice easy custom data api not like persistent containers or whatever the complicated fuck spigot uses
    val maxStackSize: Bindable<Int> = Bindable(64)
    val unbreakable: Bindable<Boolean> = Bindable(false)
    val hasGlint: Bindable<Boolean> = Bindable(false)

    private val customDataHolder = CustomDataHolder()
    var customData: Bindable<NBTCompound> = Bindable(NBT.Compound())

    fun <T : Any> setCustomData(key: String, value: T) {
        customDataHolder[key] = value
        rebuildCustomDataNbt()
    }

    fun removeCustomData(key: String) {
        customDataHolder.remove(key)
        rebuildCustomDataNbt()
    }

    fun <T: Any> getCustomDataOrNull(key: String): T? {
        updateCustomDataHolderFromComponent()
        val value = customDataHolder.dataStore[key] ?: return null
        return value as T
    }

    private fun updateCustomDataHolderFromComponent() {
        val component = components.getOrNull<CustomDataItemComponent>(CustomDataItemComponent::class) ?: return
        component.data.forEach {
            val value = when(it.value) {
                is NBTString -> (it.value as NBTString).value
                is NBTInt -> (it.value as NBTInt).value
                is NBTFloat -> (it.value as NBTFloat).value
                is NBTDouble -> (it.value as NBTDouble).value
                is NBTLong -> (it.value as NBTLong).value
                is NBTByte -> (it.value as NBTByte).value
                else -> throw UnsupportedEncodingException("${it.value::class.simpleName} is not supported in custom data nbt")
            }

            customDataHolder[it.key] = value
        }
    }

    private fun rebuildCustomDataNbt() {
        customData.value = NBT.Compound { nbt ->
            customDataHolder.dataStore.forEach {
                when(it.value) {
                    is String -> nbt.put(it.key, it.value as String)
                    is Int -> nbt.put(it.key, it.value as Int)
                    is Float -> nbt.put(it.key, it.value as Float)
                    is Double -> nbt.put(it.key, it.value as Double)
                    is Long -> nbt.put(it.key, it.value as Long)
                    is Byte -> nbt.put(it.key, it.value as Byte)
                    else -> throw UnsupportedEncodingException("${it.value::class.simpleName} is not supported in custom data nbt")
                }
            }
        }
    }

    fun <T: Any> getCustomData(key: String): T {
        return getCustomDataOrNull<T>(key) ?: throw IllegalArgumentException("Value for key $key not found in data holder")
    }

    init {
        displayName.valueChanged { components.addOrUpdate(CustomNameItemComponent(it.newValue.toComponent())) }
        lore.listUpdated { components.addOrUpdate(LoreItemComponent(lore.values.toComponents())) }
        customModelData.valueChanged { components.addOrUpdate(CustomModelDataItemComponent(it.newValue)) }
        maxStackSize.valueChanged { components.addOrUpdate(MaxStackSizeItemComponent(it.newValue)) }
        unbreakable.valueChanged { components.addOrUpdate(UnbreakableItemComponent(true)) }
        hasGlint.valueChanged { components.addOrUpdate(EnchantmentGlintOverrideItemComponent(it.newValue)) }
        customData.valueChanged { components.addOrUpdate(CustomDataItemComponent(it.newValue)) }
        if(amount <= 0) amount = 1

        //TODO this will be added back once im 100% confident item components are fully working
//        material.defaultComponents?.forEach {
//            components.add(it)
//        }
    }

    companion object {
        val AIR = ItemStack(Items.AIR, 1)
    }

    fun isEmpty(): Boolean = this.isSameAs(AIR)

    fun withAmount(amount: Int): ItemStack {
        val newItemStack = ItemStack(material, amount)
        newItemStack.components.addAll(components.values, true)
        return newItemStack
    }

    fun withAmount(amount: (Int) -> Int): ItemStack {
        val newAmount = this.amount + 0
        amount.invoke(newAmount)
        val newItemStack = ItemStack(material, newAmount)
        newItemStack.components.addAll(components.values, true)
        return newItemStack
    }

    override fun toString(): String = "ItemStack(${material.identifier}, ${components.values}, $amount)".stripComponentTags()
}

fun Collection<String>.toComponents(): Collection<Component> {
    val components = mutableListOf<Component>()
    this.forEach { components.add(it.toComponent()) }
    return components
}

fun ByteBuf.readItemStackList(): List<ItemStack> {
    val list = mutableListOf<ItemStack>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readItemStack())
    }
    return list
}

fun ByteBuf.readItemStack(): ItemStack {
    val count = this.readVarInt()
    if(count <= 0) return ItemStack.AIR

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
    itemStack.components.values.forEach { this.writeItemComponent(it) }
}

fun ItemStack.clone(): ItemStack {
    val itemStack = ItemStack(this.material, this.amount)
    itemStack.components.setValues(this.components.values.toMutableList())

    return itemStack
}

fun ItemStack.toComparisonString(): String = "ItemStack(${this.components.values};${this.material.identifier})".stripComponentTags()

fun ItemStack.isSameAs(other: ItemStack): Boolean = this.toComparisonString() == other.toComparisonString()