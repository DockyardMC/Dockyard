package io.github.dockyard.tests.item

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.item.clone
import io.github.dockyardmc.item.itemStack
import io.github.dockyardmc.registry.Items
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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
        val item = ItemStack(Items.DIAMOND_SWORD).withAmount(5).withCustomModelData(3f)
        assertEquals(item, item.clone())
    }

    @Test
    fun testFields() {
        val item = ItemStack(Items.DIAMOND_SWORD, 1)

        assertEquals(Items.DIAMOND_SWORD, item.material)
        assertEquals(1, item.amount)

        assertEquals(0, item.components.components.size)

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
}