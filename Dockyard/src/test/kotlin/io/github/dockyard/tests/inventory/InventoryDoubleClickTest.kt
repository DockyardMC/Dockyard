package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.DoubleClickButtonAction
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalButtonAction
import io.github.dockyardmc.registry.Items
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryDoubleClickTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
    }

    @AfterTest
    fun cleanup() {
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
    }

    @Test
    fun testDoubleClick() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.SWEET_BERRIES).withDisplayName("<red>very sweet berries")

        for (i in 0 until 17) { // should leave 4 in the inventory
            player.inventory[i] = itemStack.withAmount(4)
        }

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)
        assertEquals(itemStack.withAmount(4), player.inventory.cursorItem.value)

        sendSlotClick(player, 0, DoubleClickButtonAction.DOUBLE_CLICK.button, ContainerClickMode.DOUBLE_CLICK, player.inventory.cursorItem.value)
        assertEquals(itemStack.withAmount(64), player.inventory.cursorItem.value)
        assertEquals(4, player.inventory.getAmountOf(itemStack))
    }
}