package io.github.dockyard.tests.item

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.item.*
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.CustomColor
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ItemStackMetaTests {

    val color = CustomColor.fromHex("#fca3ff")

    @Test
    fun testItemStackMeta() {

        val item = itemStack {
            withMaterial(Items.DIAMOND_SWORD)
            withDisplayName("<aqua><underline>Cool Diamond Sword")
            withRarity(ItemRarity.EPIC)
            withCustomModelData(69)
            withMaxStackSize(420)
            withEnchantmentGlint(true)
            withUnbreakable(true)
            withAmount(1)
            withConsumable(2f)
            withFood(2, 0f, true)
            withUseCooldown(2f)
            withDyedColor(color)
        }

        assertEquals(11, item.components.size)
        assertContains(item.components.map { it::class }, CustomNameItemComponent::class)
        assertContains(item.components.map { it::class }, ConsumableItemComponent::class)
        assertContains(item.components.map { it::class }, FoodItemComponent::class)

        assertContains(item.components, RarityItemComponent(ItemRarity.EPIC))
        assertContains(item.components, CustomModelDataItemComponent(69))
        assertContains(item.components, MaxStackSizeItemComponent(420))
        assertContains(item.components, EnchantmentGlintOverrideItemComponent(true))
        assertContains(item.components, UnbreakableItemComponent(false))
        assertContains(item.components, UseCooldownItemComponent(2f))
        assertContains(item.components, DyedColorItemComponent(color))
    }

    @Test
    fun testMetaEditing() {
        val item = itemStack {
            withMaterial(Items.DIAMOND_SWORD)
            withDisplayName("<aqua><underline>Cool Diamond Sword")
            withRarity(ItemRarity.EPIC)
            withCustomModelData(69)
            withMaxStackSize(420)
            withEnchantmentGlint(true)
            withUnbreakable(true)
            withAmount(1)
            withConsumable(2f)
            withFood(2, 0f, true)
            withUseCooldown(2f)
            withDyedColor(color)
        }

        val modifiedItem = item.withMeta {
            withAmount(5)
            withDisplayName("<red><underline>Cool Nether Sword")
            withMaterial(Items.NETHERITE_SWORD)
        }

        assertNotEquals(item, modifiedItem)
        assertEquals(item, item)
    }
}