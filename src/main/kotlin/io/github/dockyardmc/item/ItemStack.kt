package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.extentions.put
import io.github.dockyardmc.extentions.readVarInt
import io.github.dockyardmc.extentions.writeVarInt
import io.github.dockyardmc.protocol.ProtocolWritable
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
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

data class ItemStack(
    var material: Item,
    var amount: Int = 1,
    val components: List<ItemComponent> = listOf(),
    val existingMeta: ItemStackMeta? = null,
    val attributes: Collection<AttributeModifier> = listOf()
) : ProtocolWritable {

    constructor(material: Item, amount: Int = 1, vararg components: ItemComponent, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components.toList(), attributes = attributes)
    constructor(material: Item, vararg components: ItemComponent, amount: Int = 1, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components.toList(), attributes = attributes)
    constructor(material: Item, components: List<ItemComponent>, amount: Int = 1, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components, attributes = attributes)

    init {
        if(amount <= 0) throw IllegalArgumentException("ItemStack amount cannot be less than 1")
    }

    companion object {
        val AIR = ItemStack(Items.AIR, 1)

        fun read(buffer: ByteBuf): ItemStack {
            val count = buffer.readVarInt()
            if(count <= 0) return ItemStack.AIR

            val itemId = buffer.readVarInt()
            val componentsToAdd = buffer.readVarInt()
            val componentsToRemove = buffer.readVarInt()

            val components: MutableList<ItemComponent> = mutableListOf()
            val removeComponents: MutableList<ItemComponent> = mutableListOf()

            for (i in 0 until componentsToAdd) {
                val type = buffer.readVarInt()
                val component = buffer.readComponent(type)
                components.add(component)
            }
            for (i in 0 until componentsToRemove) {
                val type = buffer.readVarInt()
            }

            var item = ItemStack(ItemRegistry.getByProtocolId(itemId), count)
            components.forEach { item = item.withComponent(it) }

            return item
        }
    }

    override fun write(buffer: ByteBuf) {
        if(this.material == Items.AIR) {
            buffer.writeVarInt(0)
            return
        }

        buffer.writeVarInt(this.amount)
        buffer.writeVarInt(this.material.getProtocolId())
        buffer.writeVarInt(this.components.size)
        buffer.writeVarInt(0)
        this.components.forEach { buffer.writeItemComponent(it) }
    }

    fun withCustomModelData(customModelData: Int): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withCustomModelData(customModelData) }.toItemStack()
    }

    fun withDisplayName(displayName: String): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withDisplayName(displayName) }.toItemStack()
    }

    fun withFood(nutrition: Int, saturation: Float = 0f, canAlwaysEat: Boolean = true): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withFood(nutrition, saturation, canAlwaysEat) }.toItemStack()
    }

    fun withHideTooltip(hideTooltip: Boolean): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withHideTooltip(hideTooltip) }.toItemStack()
    }

    fun withComponent(vararg component: ItemComponent): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withComponent(component.toList()) }.toItemStack()
    }

    fun withConsumable(
        consumeTimeSeconds: Float,
        animation: ConsumableAnimation = ConsumableAnimation.EAT,
        sound: String = Sounds.ENTITY_GENERIC_EAT,
        hasParticles: Boolean = true,
        consumeEffects: List<ConsumeEffect> = listOf()
    ): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withConsumable(consumeTimeSeconds, animation, sound, hasParticles, consumeEffects) }.toItemStack()
    }

    fun withRarity(rarity: ItemRarity): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withRarity(rarity) }.toItemStack()
    }

    fun withUnbreakable(unbreakable: Boolean): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withUnbreakable(unbreakable) }.toItemStack()
    }

    fun withMaxStackSize(maxStackSize: Int): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withMaxStackSize(maxStackSize) }.toItemStack()
    }

    fun withAmount(amount: Int): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withAmount(amount) }.toItemStack()
    }

    fun withLore(vararg lore: String): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withLore(lore.toList()) }.toItemStack()
    }

    fun withLore(lore: List<String>): ItemStack {
        return ItemStackMeta.fromItemStack(this).apply { withLore(lore) }.toItemStack()
    }

    fun withMeta(builder: ItemStackMeta.() -> Unit): ItemStack {
        val meta = ItemStackMeta.fromItemStack(this)
        meta.apply(builder)
        return meta.toItemStack()
    }

    val customModelData: Int
        get() { return components.getOrNull(CustomModelDataItemComponent::class)?.customModelData ?: 0 }

    val maxStackSize: Int
        get() { return components.getOrNull(MaxStackSizeItemComponent::class)?.maxStackSize ?: 64 }

    val unbreakable: Boolean
        get() { return components.getOrNull(UnbreakableItemComponent::class) != null }

    val hasGlint: Boolean
        get() { return components.getOrNull(EnchantmentGlintOverrideItemComponent::class)?.hasGlint ?: false }


    private val customDataHolder = CustomDataHolder()
    var customData: NBTCompound  = NBTCompound.EMPTY

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
        customData = NBT.Compound { nbt ->
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

    fun isEmpty(): Boolean = this.isSameAs(AIR)

    override fun toString(): String = "ItemStack(${material.identifier}, ${components}, $amount)".stripComponentTags()

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is ItemStack) return false
        return isSameAs(other)
    }
}

fun Collection<String>.toComponents(): Collection<Component> {
    val components = mutableListOf<Component>()
    this.forEach { components.add(it.toComponent()) }
    return components
}

fun ItemStack.clone(): ItemStack {
    return ItemStack(material, amount, components, existingMeta, attributes)
}

fun ItemStack.toComparisonString(): String = "ItemStack(${this.components};${this.material.identifier})".stripComponentTags()

fun ItemStack.isSameAs(other: ItemStack): Boolean {
    return this.toComparisonString() == other.toComparisonString()
}