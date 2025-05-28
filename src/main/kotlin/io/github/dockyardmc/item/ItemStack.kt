package io.github.dockyardmc.item

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.noxesium.Noxesium
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
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
    val components: Set<ItemComponent> = setOf(),
    val existingMeta: ItemStackMeta? = null,
    val attributes: Collection<AttributeModifier> = listOf()
) : NetworkWritable {

    constructor(material: Item, amount: Int = 1, vararg components: ItemComponent, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components.toSet(), attributes = attributes)
    constructor(material: Item, vararg components: ItemComponent, amount: Int = 1, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components.toSet(), attributes = attributes)
    constructor(material: Item, components: Set<ItemComponent>, amount: Int = 1, attributes: Collection<AttributeModifier> = listOf()) : this(material, amount, components, attributes = attributes)

    init {
        if (amount <= 0) throw IllegalArgumentException("ItemStack amount cannot be less than 1")
    }

    companion object {
        val AIR = ItemStack(Items.AIR, 1)

        fun read(buffer: ByteBuf): ItemStack {
            val count = buffer.readVarInt()
            if (count <= 0) return AIR

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
        if (this.material == Items.AIR) {
            buffer.writeVarInt(0)
            return
        }

        val itemComponents = mutableListOf<ItemComponent>()
        itemComponents.addAll(this.components)
        if (!customData.isEmpty()) {
            itemComponents.add(CustomDataItemComponent(customData))
        }

        buffer.writeVarInt(this.amount)
        buffer.writeVarInt(this.material.getProtocolId())
        buffer.writeVarInt(itemComponents.size)
        buffer.writeVarInt(0)

        itemComponents.forEach {
            buffer.writeItemComponent(it)
        }
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

    fun withAmount(amount: (Int) -> Int): ItemStack {
        return withAmount(amount.invoke(this.amount))
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

    @JvmName("withCustomModelDatafloatListFloat")
    fun withCustomModelData(floats: List<Float>): ItemStack {
        return withMeta { withCustomModelData(floats) }
    }

    @JvmName("withCustomModelDatafloatFloat")
    fun withCustomModelData(vararg float: Float): ItemStack {
        return withMeta { withCustomModelData(*float) }
    }

    @JvmName("withCustomModelDataflagsListBoolean")
    fun withCustomModelData(flags: List<Boolean>): ItemStack {
        return withMeta { withCustomModelData(flags) }
    }

    @JvmName("withCustomModelDataflagsBoolean")
    fun withCustomModelData(vararg flags: Boolean): ItemStack {
        return withMeta { withCustomModelData(*flags) }
    }

    @JvmName("withCustomModelDatastringsListString")
    fun withCustomModelData(strings: List<String>): ItemStack {
        return withMeta { withCustomModelData(strings) }
    }

    @JvmName("withCustomModelDatastringsString")
    fun withCustomModelData(vararg string: String): ItemStack {
        return withMeta { withCustomModelData(*string) }
    }

    @JvmName("withCustomModelDatacolorsListInt")
    fun withCustomModelData(colors: List<Int>): ItemStack {
        return withMeta { withCustomModelData(colors) }
    }

    @JvmName("withCustomModelDatacolorInt")
    fun withCustomModelData(vararg color: Int): ItemStack {
        return withMeta { withCustomModelData(*color) }
    }

    @JvmName("withCustomModelDatacolorListCustomColor")
    fun withCustomModelData(colors: List<CustomColor>): ItemStack {
        return withMeta { withCustomModelData(colors) }
    }

    @JvmName("withCustomModelDatacolorListCustomColor")
    fun withCustomModelData(vararg color: CustomColor): ItemStack {
        return withMeta { withCustomModelData(*color) }
    }

    @JvmName("withCustomModelDatawhatthefuckaaaaaaa")
    fun withCustomModelData(floats: List<Float> = listOf(), flags: List<Boolean> = listOf(), strings: List<String> = listOf(), colors: List<Int> = listOf()): ItemStack {
        return withMeta { withCustomModelData(floats, flags, strings, colors) }
    }

    val customModelData: CustomModelDataItemComponent
        get() {
            return components.getOrNull(CustomModelDataItemComponent::class) ?: CustomModelDataItemComponent()
        }

    val maxStackSize: Int
        get() {
            return components.getOrNull(MaxStackSizeItemComponent::class)?.maxStackSize ?: material.maxStack
        }

    val unbreakable: Boolean
        get() {
            return components.getOrNull(UnbreakableItemComponent::class) != null
        }

    val hasGlint: Boolean
        get() {
            return components.getOrNull(EnchantmentGlintOverrideItemComponent::class)?.hasGlint ?: false
        }

    var noxesiumImmovable: Boolean
        set(value) {
            if (value) {
                setCustomData<NBTCompound>(Noxesium.BUKKIT_TAG, Noxesium.BUKKIT_COMPOUND)
            } else {
                removeCustomData(Noxesium.BUKKIT_TAG)
            }
        }
        get() {
            val tag = getCustomDataOrNull<NBTCompound>(Noxesium.BUKKIT_TAG)?.getAsBoolean(Noxesium.IMMOVABLE_TAG) ?: false
            return tag
        }

    val customDataHolder = CustomDataHolder()
    var customData: NBTCompound = NBTCompound.EMPTY

    fun <T : Any> setCustomData(key: String, value: T) {
        customDataHolder[key] = value
        rebuildCustomDataNbt()
    }

    fun removeCustomData(key: String) {
        customDataHolder.remove(key)
        rebuildCustomDataNbt()
    }

    inline fun <reified T : Any> getCustomDataOrNull(key: String): T? {
        updateCustomDataHolderFromComponent()
        val value = customDataHolder.dataStore[key] ?: return null
        if (T::class == Boolean::class) {
            return (value as Byte?)?.toBoolean() as T?
        }
        return value as T?
    }

    fun withNoxesiumImmovable(immovable: Boolean): ItemStack {
        noxesiumImmovable = immovable
        return this
    }

    fun updateCustomDataHolderFromComponent() {
        val component = components.getOrNull<CustomDataItemComponent>(CustomDataItemComponent::class)
        if (component == null) {
            log("Custom Data Component Not Found: $components", LogType.CRITICAL)
            return
        }

        component.data.forEach {
            val value: Any = when (it.value) {
                is NBTString -> (it.value as NBTString).value
                is NBTInt -> (it.value as NBTInt).value
                is NBTFloat -> (it.value as NBTFloat).value
                is NBTDouble -> (it.value as NBTDouble).value
                is NBTLong -> (it.value as NBTLong).value
                is NBTByte -> (it.value as NBTByte).value
                is NBTCompound -> it.value as NBTCompound
                else -> throw UnsupportedEncodingException("${it.value::class.simpleName} is not supported in custom data nbt")
            }

            customDataHolder[it.key] = value
        }
    }

    private fun rebuildCustomDataNbt() {
        customData = NBT.Compound { nbt ->
            customDataHolder.dataStore.forEach {
                when (it.value) {
                    is String -> nbt.put(it.key, it.value as String)
                    is Int -> nbt.put(it.key, it.value as Int)
                    is Float -> nbt.put(it.key, it.value as Float)
                    is Double -> nbt.put(it.key, it.value as Double)
                    is Long -> nbt.put(it.key, it.value as Long)
                    is Byte -> nbt.put(it.key, it.value as Byte)
                    is Boolean -> nbt.put(it.key, (it.value as Boolean).toByte())
                    is NBTCompound -> nbt.put(it.key, it.value as NBTCompound)
                    else -> throw UnsupportedEncodingException("${it.value::class.simpleName} is not supported in custom data nbt")
                }
            }
        }
    }

    inline fun <reified T : Any> getCustomData(key: String): T {
        return getCustomDataOrNull<T>(key) ?: throw IllegalArgumentException("Value for key $key not found in data holder")
    }

    fun isEmpty(): Boolean = this.isSameAs(AIR)

    override fun toString(): String = "ItemStack(${material.identifier}, ${components}, $amount)".stripComponentTags()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ItemStack) return false
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