package io.github.dockyardmc.item

import io.github.dockyardmc.extentions.fromRGBInt
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.ProfileProperty
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.Item
import io.github.dockyardmc.scroll.CustomColor
import java.util.UUID

class ItemBuilder() {

    private lateinit var itemStack: ItemStack

    constructor(item: Item, amount: Int = 1) : this() {
        this.itemStack = ItemStack(item, amount)
    }

    constructor(itemStack: ItemStack) : this() {
        this.itemStack = itemStack
    }

    /**
     * Build the item
     *
     * @return ItemStack
     */
    fun build(): ItemStack {
        return itemStack
    }

    /**
     * Set the amount of the item
     *
     * @param amount Int
     * @return ItemBuilder
     *
     * @throws IllegalArgumentException if the amount is less than 1
     */
    fun withAmount(amount: Int): ItemBuilder {
        if (amount < 1) {
            throw IllegalArgumentException("Amount cannot be less than 1")
        }
        itemStack.amount = amount
        return this
    }

    /**
     * Set the name of the item
     *
     * @param name String
     * @return ItemBuilder
     */
    fun withName(name: String): ItemBuilder {
        itemStack.displayName.value = name
        return this
    }

    /**
     * Set the lore of the item
     *
     * @param lore List<String>
     * @return ItemBuilder
     */
    fun withLore(lore: List<String>): ItemBuilder {
        itemStack.lore.setValues(lore)
        return this
    }

    /**
     * Adds a line of lore to the item
     *
     * @param lore String
     * @return ItemBuilder
     */
    fun addLore(lore: String): ItemBuilder {
        itemStack.lore.add(lore)
        return this
    }

    /**
     * Set the custom model data of the item
     *
     * @param customModelData Int
     * @return ItemBuilder
     */
    fun withCustomModelData(customModelData: Int): ItemBuilder {
        itemStack.customModelData.value = customModelData
        return this
    }

    /**
     * Set the max stack size of the item
     *
     * @param maxStackSize Int
     * @return ItemBuilder
     *
     * @throws IllegalArgumentException if the max stack size is less than 1
     */
    fun withMaxStackSize(maxStackSize: Int): ItemBuilder {
        if (maxStackSize < 1) {
            throw IllegalArgumentException("Max stack size cannot be less than 1")
        }
        itemStack.maxStackSize.value = maxStackSize
        return this
    }

    /**
     * Set the damage of the item
     *
     *
     * @param damage Int
     * @param maxDamage Int (default 100)
     * @return ItemBuilder
     *
     * @throws IllegalArgumentException if the damage is less than 0
     * @throws IllegalArgumentException if the damage is greater than the max damage
     * @throws IllegalArgumentException if the item is stackable
     */
    fun withDamage(damage: Int, maxDamage: Int = 100): ItemBuilder {
        if (damage < 0) {
            throw IllegalArgumentException("Damage cannot be less than 0")
        }
        if (damage > maxDamage) {
            throw IllegalArgumentException("Damage cannot be greater than max damage")
        }
        if (itemStack.maxStackSize.value > 1) {
            throw IllegalArgumentException("Cannot set damage on stackable items")
        }

        itemStack.components.addOrUpdate(DamageItemComponent(damage))
        itemStack.components.addOrUpdate(MaxDamageItemComponent(maxDamage))
        return this
    }

    /**
     * Set the unbreakable state of the item
     *
     * @param unbreakable Boolean
     * @return ItemBuilder
     */
    fun isUnbreakable(unbreakable: Boolean, showInTooltip: Boolean = false): ItemBuilder {
        if (unbreakable) {
            itemStack.components.addOrUpdate(UnbreakableItemComponent(showInTooltip))
        } else {
            itemStack.components.removeByType((UnbreakableItemComponent()::class))
        }
        return this
    }

    /**
     * Set the glint of the item
     *
     * @param glint Boolean
     * @return ItemBuilder
     */
    fun withGlint(glint: Boolean): ItemBuilder {
        itemStack.hasGlint.value = glint
        return this
    }

    /**
     * Set the material of the item
     *
     * @param material Item
     * @return ItemBuilder
     */
    fun withMaterial(material: Item): ItemBuilder {
        itemStack.material = material
        return this
    }

    /**
     * Set the color of the item
     *
     * @param color RGB int color
     * @param showInTooltip Boolean (default false)
     * @return ItemBuilder
     */
    fun withColor(color: Int, showInTooltip: Boolean = false): ItemBuilder {
        itemStack.components.addOrUpdate(DyedColorItemComponent(CustomColor.fromRGBInt(color), showInTooltip))
        return this
    }

    /**
     * Set the color of the item
     *
     * @param color Hexadecimal color
     * @param showInTooltip Boolean (default false)
     * @return ItemBuilder
     */
    fun withColor(color: String, showInTooltip: Boolean = false): ItemBuilder {
        itemStack.components.addOrUpdate(DyedColorItemComponent(CustomColor.fromHex(color), showInTooltip))
        return this
    }

    /**
     * Set the food of the item
     *
     * @param nutrition Int
     * @param giveSaturation Boolean (default false)
     * @param canAlwaysEat Boolean (default true)
     * @param secondsToEat Float (default 2f)
     * @return ItemBuilder
     */
    @Deprecated("Breaks in 1.21.2. Will be reworked in the future")
    fun withFood(nutrition: Int, giveSaturation: Boolean = false, canAlwaysEat: Boolean = true , secondsToEat: Float = 2f): ItemBuilder {
        itemStack.components.addOrUpdate(FoodItemComponent(nutrition, giveSaturation, canAlwaysEat, secondsToEat))
        return this
    }

    /**
     * Sets position for a compass to point to
     *
     * @param location Location
     * @param tracked Boolean (default false)
     * @return ItemBuilder
     *
     * @throws IllegalArgumentException if the item is not a compass
     */
    fun withLodestoneTracker(location: Location, tracked: Boolean = false): ItemBuilder {
        if (itemStack.material != Items.COMPASS) {
            throw IllegalArgumentException("Item must be a compass")
        }

        itemStack.components.addOrUpdate(LodestoneTrackerItemComponent(true, location.world, location, tracked))

        return this
    }


    /**
     * Sets the profile of the player head
     *
     * @param username String (default null)
     * @param uuid UUID (default null)
     * @param profile ProfileProperty (default null)
     * @return ItemBuilder
     *
     * @throws IllegalArgumentException if the item is not a player head
     */
    fun withProfile(username: String? = null, uuid: UUID? = null, profile: ProfileProperty? = null): ItemBuilder {
        if (itemStack.material != Items.PLAYER_HEAD) {
            throw IllegalArgumentException("Item must be a player head")
        }
        if (username == null && uuid == null && profile == null) {
            throw IllegalArgumentException("At least one of the parameters must be set")
        }

        itemStack.components.addOrUpdate(PlayerHeadProfileItemComponent(username, uuid, ProfilePropertyMap(username.orEmpty(),
            if (profile != null) mutableListOf(profile) else mutableListOf<ProfileProperty>())
        ))

        return this
    }

}