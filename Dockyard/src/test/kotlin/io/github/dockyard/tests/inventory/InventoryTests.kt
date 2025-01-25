package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.assertSlot
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCloseContainerPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.registries.ItemRegistry
import io.github.dockyardmc.ui.examples.ExampleCookieClickerScreen
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryTests {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
    }

    @Test
    fun testInventoryOpen() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        assertEquals(null, player.currentOpenInventory)
        assertEquals(false, player.hasInventoryOpen)

        player.openInventory(ExampleCookieClickerScreen(player))
        assertEquals(true, player.currentOpenInventory is ExampleCookieClickerScreen)
        assertEquals(true, player.hasInventoryOpen)

        PlayerTestUtil.sendPacket(player, ServerboundCloseContainerPacket(1))

        assertEquals(null, player.currentOpenInventory)
        assertEquals(false, player.hasInventoryOpen)
    }

    @Test
    fun testInventoryClose() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val itemStack = ItemStack(Items.SWEET_BERRIES, 5)
        player.clearInventory()

        player.inventory.cursorItem.value = itemStack
        player.closeInventory()

        assertSlot(player, 0, itemStack)
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
    }

    @Test
    fun testGive() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()

        val itemStack = ItemStack(Items.COOKIE).withAmount(2).withDisplayName("<orange>Magical cookie")

        player.give(Items.STONE)
        player.give(Items.TNT)
        player.give(Items.FLINT_AND_STEEL)
        player.give(Items.FLINT_AND_STEEL)
        player.give(itemStack)

        for (i in 0 until 4) {
            player.give(ItemRegistry.items.values.random())
        }
        player.give(itemStack)

        assertSlot(player, 0, Items.STONE)
        assertSlot(player, 1, Items.TNT)
        assertSlot(player, 2, Items.FLINT_AND_STEEL)
        assertSlot(player, 3, Items.FLINT_AND_STEEL)
        assertSlot(player, 4, itemStack.withAmount(4))

        player.clearInventory()

        player.inventory[3] = itemStack

        player.give(itemStack)
        assertSlot(player, 0, ItemStack.AIR)
        assertSlot(player, 3, itemStack.withAmount(4))
    }

}
