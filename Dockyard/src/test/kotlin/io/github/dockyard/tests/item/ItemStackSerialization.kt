package io.github.dockyard.tests.item

import io.github.dockyardmc.item.*
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.CustomColor
import io.netty.buffer.Unpooled
import kotlin.test.*

class ItemStackSerialization {

    @Test
    fun testSerializeAndDeserialize() {
        val color = CustomColor.fromHex("#fca3ff")

        val itemStack = itemStack {
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

        val buffer = Unpooled.buffer()
        itemStack.write(buffer)

        val readStack = ItemStack.read(buffer)

        assertEquals(itemStack.material, readStack.material)
        assertEquals(itemStack.amount, readStack.amount)
        assertEquals(itemStack.attributes.size, readStack.attributes.size)
        assertEquals(itemStack.components.size, readStack.components.size)

        assertContains(readStack.components.map { it::class }, CustomNameItemComponent::class)
        assertContains(readStack.components.map { it::class }, ConsumableItemComponent::class)
        assertContains(readStack.components.map { it::class }, FoodItemComponent::class)
        assertContains(readStack.components, RarityItemComponent(ItemRarity.EPIC))
        assertContains(readStack.components, CustomModelDataItemComponent(69))
        assertContains(readStack.components, MaxStackSizeItemComponent(420))
        assertContains(readStack.components, EnchantmentGlintOverrideItemComponent(true))
        assertContains(readStack.components, UnbreakableItemComponent(false))
        assertContains(readStack.components, UseCooldownItemComponent(2f))
        assertContains(readStack.components, DyedColorItemComponent(color))
    }

    @Test
    fun testItemStackCustomData() {
        val item = ItemStack(Items.DIAMOND_SWORD).withAmount(3).withCustomModelData(5)
        item.setCustomData<Int>("waifus", 10)
        item.setCustomData<String>("uwu", "nya")

        val buffer = Unpooled.buffer()
        item.write(buffer)

        val readStack = ItemStack.read(buffer)
        assertEquals(10, readStack.getCustomData<Int>("waifus"))
        assertEquals("nya", readStack.getCustomData<String>("uwu"))
    }
}