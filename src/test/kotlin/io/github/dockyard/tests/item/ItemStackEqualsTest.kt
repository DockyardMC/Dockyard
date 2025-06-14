package io.github.dockyard.tests.item

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.components.ConsumableComponent
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.types.ItemRarity
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.Sounds
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ItemStackEqualsTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEquals() {
        val bigBoi = ItemStack(Items.FARMLAND).withMeta {
            withFood(4, 1.3f, true)
            withRarity(ItemRarity.EPIC)
            withEnchantmentGlint(true)
            withMaxStackSize(60)
            withCustomModelData(3f)
            withDisplayName("<red><u>test")
            withLore("test", "yo", ":3")
        }

        val bigBoi2 = ItemStack(Items.FARMLAND).withMeta {
            withFood(4, 1.3f, true)
            withRarity(ItemRarity.EPIC)
            withEnchantmentGlint(true)
            withMaxStackSize(60)
            withCustomModelData(3f)
            withDisplayName("<red><u>test")
            withLore("test", "yo", ":3")
        }

        val notMatchingBigBoi = ItemStack(Items.FARMLAND).withMeta {
            withFood(4, 1.3f, true)
            withRarity(ItemRarity.EPIC)
            withEnchantmentGlint(true)
            withMaxStackSize(60)
            withCustomModelData(3f)
            withDisplayName("<red><u>testing")
            withLore("test", "yo", ":3")
        }

        val shouldEqual = mutableMapOf(
            ItemStack(Items.DIAMOND_SWORD).withAmount(3) to ItemStack(Items.DIAMOND_SWORD).withAmount(3),
            ItemStack(Items.DIAMOND_SWORD).withConsumable(1.5f, ConsumableComponent.Animation.BOW, Sounds.ITEM_BOOK_PUT, true, listOf()) to ItemStack(Items.DIAMOND_SWORD).withConsumable(1.5f, ConsumableComponent.Animation.BOW, Sounds.ITEM_BOOK_PUT, true, listOf()),
            bigBoi to bigBoi2,
        )

        shouldEqual.forEach { (first, second) ->
            assertEquals(first, second)
        }

        assertNotEquals(bigBoi, notMatchingBigBoi)
    }
}