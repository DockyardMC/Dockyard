package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.assertSlot
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalShiftButtonAction
import io.github.dockyardmc.registry.Items
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class InventoryShiftClickTest {

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
    fun testShift() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)

        listOf(NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK.button, NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK.button).forEach { button ->
            player.clearInventory()
            player.inventory[0] = itemStack

            sendSlotClick(player, 0, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)

            assertSlot(player, 0, ItemStack.AIR)
            assertSlot(player, 9, itemStack)

            sendSlotClick(player, 9, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)

            assertSlot(player, 0, itemStack)
            assertSlot(player, 9, ItemStack.AIR)

            player.inventory[9] = ItemStack(Items.COOKIE)
            player.inventory[10] = ItemStack(Items.FLINT_AND_STEEL)
            player.inventory[12] = ItemStack(Items.DIAMOND)

            sendSlotClick(player, 0, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)

            assertSlot(player, 0, ItemStack.AIR)
            assertSlot(player, 11, itemStack)

            sendSlotClick(player, 10, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)
            sendSlotClick(player, 9, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)
            sendSlotClick(player, 11, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)

            assertSlot(player, 9, ItemStack.AIR)
            assertSlot(player, 10, ItemStack.AIR)
            assertSlot(player, 11, ItemStack.AIR)

            assertSlot(player, 0, Items.FLINT_AND_STEEL)
            assertSlot(player, 1, Items.COOKIE)
            assertSlot(player, 2, itemStack)
        }
    }

    @Test
    fun testShiftStacking() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)

        listOf(NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK.button, NormalShiftButtonAction.SHIFT_RIGHT_MOUSE_CLICK.button).forEach { button ->
            player.clearInventory()

            player.inventory[0] = itemStack.withAmount(3)
            player.inventory[1] = itemStack.withAmount(3)

            sendSlotClick(player, 0, button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)
            assertSlot(player, 0, ItemStack.AIR)
            assertSlot(player, 9, itemStack.withAmount(3))

            sendSlotClick(player, 9, button, ContainerClickMode.NORMAL_SHIFT, itemStack.withAmount(3))
            assertSlot(player, 0, ItemStack.AIR)
            assertSlot(player, 1, itemStack.withAmount(6))

            sendSlotClick(player, 1, button, ContainerClickMode.NORMAL_SHIFT, itemStack)
            assertSlot(player, 0, ItemStack.AIR)
            assertSlot(player, 1, ItemStack.AIR)
            assertSlot(player, 9, itemStack)
        }
    }
}