package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.assertSlot
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.EquipmentSlot
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalButtonAction
import io.github.dockyardmc.registry.Items
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryEquipmentTest {

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
    fun testClicking() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val chestplate = ItemStack(Items.IRON_CHESTPLATE)

        player.inventory.cursorItem.value = chestplate
        sendSlotClick(player, 42, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, chestplate)

        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
        assertEquals(chestplate, player.equipment[EquipmentSlot.CHESTPLATE])
        assertSlot(player, 42, chestplate)

        sendSlotClick(player, 42, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)

        assertEquals(chestplate, player.inventory.cursorItem.value)
        assertEquals(ItemStack.AIR, player.equipment[EquipmentSlot.CHESTPLATE])
        assertSlot(player, 42, ItemStack.AIR)
    }
}