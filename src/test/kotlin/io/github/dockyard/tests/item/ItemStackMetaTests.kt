package io.github.dockyard.tests.item

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.components.*
import io.github.dockyardmc.item.*
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.CustomColor
import org.junit.jupiter.api.Test
import kotlin.test.*

class ItemStackMetaTests {

    val color = CustomColor.fromHex("#fca3ff")

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }

    @Test
    fun testItemStackMeta() {

        val item = itemStack {
            withMaterial(Items.DIAMOND_SWORD)
            withDisplayName("<aqua><underline>Cool Diamond Sword")
            withRarity(ItemRarity.EPIC)
            withCustomModelData(69f)
            withMaxStackSize(99)
            withEnchantmentGlint(true)
            withUnbreakable(false)
            withAmount(1)
            withConsumable(2f)
            withFood(2, 0f, true)
            withUseCooldown(2f)
            withDyedColor(color)
        }

        assertEquals(10, item.components.components.size)
        assertTrue(item.components.get<CustomNameComponent>() != null)
        assertTrue(item.components.get<ConsumableComponent>() != null)
        assertTrue(item.components.get<FoodComponent>() != null)

        assertEquals(ItemRarity.EPIC, item.components.get<RarityComponent>()?.rarity)
        assertEquals(listOf(69f), item.components.get<CustomModelDataComponent>()?.floats)
        assertEquals(99, item.components.get<MaxStackSizeComponent>()?.size)
        assertNotNull(item.components.get<EnchantmentGlintOverrideComponent>())
        assertNull(item.components.get<UnbreakableComponent>())
        assertEquals(2f, item.components.get<UseCooldownComponent>()?.seconds)
        assertEquals(color, item.components.get<DyedColorComponent>()?.color)
    }

    @Test
    fun testMetaEditing() {
        val item = itemStack {
            withMaterial(Items.DIAMOND_SWORD)
            withDisplayName("<aqua><underline>Cool Diamond Sword")
            withRarity(ItemRarity.EPIC)
            withCustomModelData(69f)
            withMaxStackSize(420)
            withEnchantmentGlint(true)
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