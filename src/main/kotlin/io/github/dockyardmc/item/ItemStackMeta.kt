package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.data.DataComponent
import io.github.dockyardmc.data.DataComponentPatch
import io.github.dockyardmc.data.components.*
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import java.util.*

class ItemStackMeta {
    var material: Item = Items.AIR
    var amount: Int = 1
    var lore: MutableList<String> = mutableListOf()
    var attributes: MutableSet<AttributeModifier> = mutableSetOf()

    var components: DataComponentPatch = DataComponentPatch.patchNetworkType(DataComponentPatch.EMPTY.components)

    companion object {
        fun fromItemStack(stack: ItemStack): ItemStackMeta {
            val meta = ItemStackMeta()
            meta.material = stack.material
            meta.amount = stack.amount
            meta.lore = stack.existingMeta?.lore ?: mutableListOf()
            meta.components = stack.components
            meta.attributes = stack.attributes.toMutableSet()

            return meta
        }
    }

    fun withComponent(vararg component: DataComponent) {
        var newComponents = components
        component.forEach { c ->
            newComponents = newComponents.set(c)
        }
        components = newComponents
    }

    fun withComponent(components: List<DataComponent>) {
        var newComponents = this.components
        components.forEach { c ->
            newComponents = newComponents.set(c)
        }
        this.components = newComponents
    }

    fun withDisplayName(displayName: String) {
        components = components.set(CustomNameComponent("<r></u>$displayName"))
    }

    fun withDyedColor(color: CustomColor) {
        components[DyedColorComponent(color)]
    }

    fun withGlider() {
        components[GliderComponent()]
    }

    fun withUseCooldown(cooldownSeconds: Float) {
        components = components.set(UseCooldownComponent(cooldownSeconds))
    }

    fun withProfile(username: String? = null, uuid: UUID? = null, profile: ProfileProperty? = null) {
        if (material != Items.PLAYER_HEAD) {
            throw IllegalArgumentException("Item must be a player head")
        }
        if (username == null && uuid == null && profile == null) {
            throw IllegalArgumentException("At least one of the parameters must be set")
        }

        components = components.set(
            if (profile == null) {
                ProfileComponent(username, uuid, emptyList())
            } else {
                ProfileComponent(username, uuid, listOf(ProfileComponent.Property("textures", profile.value, profile.signature)))
            }
        )
    }

    fun withConsumable(
        consumeTimeSeconds: Float,
        animation: ConsumableComponent.Animation = ConsumableComponent.Animation.EAT,
        sound: String = Sounds.ENTITY_GENERIC_EAT,
        hasParticles: Boolean = true,
        consumeEffects: List<ConsumeEffect> = listOf()
    ) {
        components = components.set(
            ConsumableComponent(
                consumeTimeSeconds,
                animation,
                sound,
                hasParticles,
                consumeEffects
            )
        )
    }

    fun buildLoreComponent() {
        if (lore.isEmpty()) return
        val component = LoreComponent(lore.map { line -> "<r><gray>$line".toComponent() })
        components = components.set(component)
    }

    fun toItemStack(): ItemStack {
        return itemStack(this)
    }

    fun withFood(nutrition: Int, saturation: Float = 0f, canAlwaysEat: Boolean = true) {
        components = components.set(FoodComponent(nutrition, saturation, canAlwaysEat))
    }

    fun withAttributes(attributes: List<AttributeModifier>) {
        this.attributes = attributes.toMutableSet()
    }

    fun addAttribute(vararg attributes: AttributeModifier) {
        this.attributes.addAll(attributes)
    }

    fun withRarity(rarity: ItemRarity) {
        components = components.set(RarityComponent(rarity))
    }

    fun withEnchantmentGlint(hasGlint: Boolean) {
        hasEnchantmentGlint(hasGlint)
    }

    fun hasEnchantmentGlint(hasGlint: Boolean) {
        components = components.set(EnchantmentGlintOverrideComponent(hasGlint))
    }

    fun isUnbreakable(unbreakable: Boolean) {
        components = if (unbreakable) components.set(UnbreakableComponent()) else components.remove(UnbreakableComponent::class)
    }

    fun withUnbreakable(unbreakable: Boolean) {
        isUnbreakable(unbreakable)
    }

    fun withMaxStackSize(maxStackSize: Int) {
        components = components.set(MaxStackSizeComponent(maxStackSize))
    }

    @JvmName("withCustomModelDatafloatListFloat")
    fun withCustomModelData(floats: List<Float>) {
        components = components.set(CustomModelDataComponent(floats, emptyList(), emptyList(), emptyList()))
    }

    @JvmName("withCustomModelDatafloatFloat")
    fun withCustomModelData(vararg float: Float) {
        withCustomModelData(float.toList())
    }

    @JvmName("withCustomModelDataflagsListBoolean")
    fun withCustomModelData(flags: List<Boolean>) {
        components = components.set(CustomModelDataComponent(emptyList(), flags, emptyList(), emptyList()))
    }

    @JvmName("withCustomModelDataflagsBoolean")
    fun withCustomModelData(vararg flags: Boolean) {
        withCustomModelData(flags.toList())
    }

    @JvmName("withCustomModelDatastringsListString")
    fun withCustomModelData(strings: List<String>) {
        components = components.set(CustomModelDataComponent(emptyList(), emptyList(), strings, emptyList()))
    }

    @JvmName("withCustomModelDatastringsString")
    fun withCustomModelData(vararg string: String) {
        withCustomModelData(string.toList())
    }


    @JvmName("withCustomModelDatacolorListCustomColor")
    fun withCustomModelData(colors: List<CustomColor>) {
        components = components.set(CustomModelDataComponent(listOf(), listOf(), listOf(), colors))
    }

    @JvmName("withCustomModelDatacolorListCustomColor")
    fun withCustomModelData(vararg color: CustomColor) {
        withCustomModelData(color.toList())
    }

    @JvmName("withCustomModelDatawhatthefuckaaaaaaa")
    fun withCustomModelData(floats: List<Float> = listOf(), flags: List<Boolean> = listOf(), strings: List<String> = listOf(), colors: List<CustomColor> = listOf()) {
        components = components.set(CustomModelDataComponent(floats, flags, strings, colors))
    }

    fun withMaterial(item: Item) {
        material = item
    }

    fun withAmount(amount: Int) {
        if (amount <= 0) this.amount = 1 else this.amount = amount
    }

    fun clearLore() {
        this.lore.clear()
    }

    fun withLore(lore: List<String>) {
        this.lore = lore.toMutableList()
    }

    fun withLore(vararg lore: String) {
        this.lore = lore.toMutableList()
    }

    fun addLore(vararg line: String) {
        this.lore.addAll(line)
    }

    fun removeLore(index: Int) {
        this.lore.removeAt(index)
    }

    fun removeLore(vararg line: String) {
        this.lore.removeAll(line.toSet())
    }
}

fun itemStack(builder: ItemStackMeta.() -> Unit): ItemStack {
    val meta: ItemStackMeta = ItemStackMeta()
    builder.invoke(meta)

    meta.buildLoreComponent()
    val itemStack = ItemStack(meta.material, meta.amount, meta.components, meta, meta.attributes)
    return itemStack.clone()
}

fun itemStack(meta: ItemStackMeta): ItemStack {
    meta.buildLoreComponent()
    val itemStack = ItemStack(meta.material, meta.amount, meta.components, meta, meta.attributes)
    return itemStack.clone()
}