package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.Sound
import java.util.*

class ItemStackMeta {
    var material: Item = Items.AIR
    var amount: Int = 1
    var lore: MutableList<String> = mutableListOf()
    var attributes: MutableList<AttributeModifier> = mutableListOf()
    var components: MutableList<ItemComponent> = mutableListOf()

    companion object {
        fun fromItemStack(stack: ItemStack): ItemStackMeta {
            val meta = ItemStackMeta()
            meta.material = stack.material
            meta.amount = stack.amount
            meta.lore = stack.existingMeta?.lore ?: mutableListOf()
            meta.components = stack.components.toMutableList()
            meta.attributes = stack.attributes.toMutableList()

            return meta
        }
    }

    fun withComponent(vararg component: ItemComponent) {
        component.forEach(components::add)
    }

    fun withComponent(components: List<ItemComponent>) {
        components.forEach(this.components::add)
    }

    fun withDisplayName(displayName: String) {
        components.addOrUpdate(CustomNameItemComponent("<r></u>$displayName".toComponent()))
    }

    fun withDyedColor(color: CustomColor) {
        components.addOrUpdate(DyedColorItemComponent(color))
    }

    fun isGlider(glider: Boolean = true) {
        withGlider(glider)
    }

    fun withGlider(glider: Boolean = true) {
        if (glider) components.remove(GliderItemComponent()) else components.addOrUpdate(GliderItemComponent())
    }

    fun withUseCooldown(cooldownSeconds: Float) {
        components.addOrUpdate(UseCooldownItemComponent(cooldownSeconds))
    }

    fun withProfile(username: String? = null, uuid: UUID? = null, profile: ProfileProperty? = null) {
        if (material != Items.PLAYER_HEAD) {
            throw IllegalArgumentException("Item must be a player head")
        }
        if (username == null && uuid == null && profile == null) {
            throw IllegalArgumentException("At least one of the parameters must be set")
        }

        components.addOrUpdate(
            PlayerHeadProfileItemComponent(
                username, uuid, ProfilePropertyMap(
                    username.orEmpty(),
                    if (profile != null) mutableListOf(profile) else mutableListOf<ProfileProperty>()
                )
            )
        )
    }

    fun withConsumable(
        consumeTimeSeconds: Float,
        animation: ConsumableAnimation = ConsumableAnimation.EAT,
        sound: String = Sounds.ENTITY_GENERIC_EAT,
        hasParticles: Boolean = true,
        consumeEffects: List<ConsumeEffect> = listOf()
    ) {
        components.addOrUpdate(
            ConsumableItemComponent(
                consumeTimeSeconds,
                animation,
                Sound(sound),
                hasParticles,
                consumeEffects
            )
        )
    }

    fun buildLoreComponent() {
        val component = LoreItemComponent(lore.map { "<r><gray>$it" }.toComponents())
        components.addOrUpdate(component)
    }

    fun toItemStack(): ItemStack {
        return itemStack(this)
    }

    fun withFood(nutrition: Int, saturation: Float = 0f, canAlwaysEat: Boolean = true) {
        components.addOrUpdate(FoodItemComponent(nutrition, saturation, canAlwaysEat))
    }

    fun withAttributes(attributes: List<AttributeModifier>) {
        this.attributes = attributes.toMutableList()
    }

    fun addAttribute(vararg attributes: AttributeModifier) {
        this.attributes.addAll(attributes)
    }

    fun withRarity(rarity: ItemRarity) {
        components.addOrUpdate(RarityItemComponent(rarity))
    }

    fun withEnchantmentGlint(hasGlint: Boolean) {
        hasEnchantmentGlint(hasGlint)
    }

    fun hasEnchantmentGlint(hasGlint: Boolean) {
        components.addOrUpdate(EnchantmentGlintOverrideItemComponent(hasGlint))
    }

    fun isUnbreakable(unbreakable: Boolean) {
        if(unbreakable) components.add(UnbreakableItemComponent()) else components.removeByType(UnbreakableItemComponent::class)
    }

    fun withUnbreakable(unbreakable: Boolean) {
        isUnbreakable(unbreakable)
    }

    fun withMaxStackSize(maxStackSize: Int) {
        components.addOrUpdate(MaxStackSizeItemComponent(maxStackSize))
    }

    fun withCustomModelData(customModelData: Int) {
        components.addOrUpdate(CustomModelDataItemComponent(customModelData))
    }

    fun withMaterial(item: Item) {
        material = item
    }

    fun withAmount(amount: Int) {
        if(amount <= 0) this.amount = 1 else this.amount = amount
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
    val itemStack = ItemStack(meta.material, meta.amount, meta.components.toSet(), meta, meta.attributes)
    return itemStack.clone()
}

fun itemStack(meta: ItemStackMeta): ItemStack {
    meta.buildLoreComponent()
    val itemStack = ItemStack(meta.material, meta.amount, meta.components.toSet(), meta, meta.attributes)
    return itemStack.clone()
}
