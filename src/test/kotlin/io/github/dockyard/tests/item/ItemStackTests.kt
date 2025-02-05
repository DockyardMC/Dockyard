package io.github.dockyard.tests.item

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.item.*
import io.github.dockyardmc.registry.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import kotlin.test.*

class ItemStackTests {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }

    @Test
    fun cloneTest() {
        val item = ItemStack(Items.DIAMOND_SWORD).withAmount(5).withCustomModelData(3)
        assertEquals(item, item.clone())
    }

    @Test
    fun testFields() {
        val item = ItemStack(Items.DIAMOND_SWORD, 1)

        assertEquals(Items.DIAMOND_SWORD, item.material)
        assertEquals(1, item.amount)

        //TODO Default Components
        assertEquals(0, item.components.size)

        val itemWithAmount = item.withAmount(5)
        assertEquals(5, itemWithAmount.amount)

        val multipliedItem = itemWithAmount.withAmount { it * 5 }
        assertEquals(25, multipliedItem.amount)

        val itemWithMeta = itemStack {
            withMaterial(Items.DIAMOND_SWORD)
            withDisplayName("Cool Diamond Sword")
        }

        assertNull(item.existingMeta)
        assertNotNull(itemWithMeta.existingMeta)
    }

    @Test
    fun testNegativeAmounts() {
        assertThrows<IllegalArgumentException> { ItemStack(Items.TNT, 0) }
        assertThrows<IllegalArgumentException> { ItemStack(Items.TNT, -1) }

        assertDoesNotThrow { ItemStack(Items.TNT, 1) }
        assertDoesNotThrow { ItemStack(Items.TNT, Int.MAX_VALUE) }
    }

    @Test
    fun testEquality() {
        val customItem = itemStack {
            withMaterial(Items.COOKIE)
            addLore("<rainbow>AAAAAAAAAAAAAAAAA")
            withCustomModelData(69)
            withMaxStackSize(420)
            withRarity(ItemRarity.EPIC)
        }

        val customItemButSameButDifferent = itemStack {
            withMaterial(Items.COOKIE)
            addLore("<rainbow>AAAAAAAAAAAAAAAAA")
            withCustomModelData(69)
            withMaxStackSize(420)
            withRarity(ItemRarity.EPIC)
        }

        val basicItem = ItemStack(Items.COOKIE)

        assertEquals(ItemStack(Items.COOKIE), basicItem)
        assertEquals(customItem, customItemButSameButDifferent)
        assertNotEquals(ItemStack(Items.COOKIE), customItem)
    }
}