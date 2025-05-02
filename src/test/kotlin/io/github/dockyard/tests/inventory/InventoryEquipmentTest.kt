package io.github.dockyard.tests.inventory

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.assertSlot
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.InventoryClickEvent
import io.github.dockyardmc.events.PlayerEquipEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalButtonAction
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalShiftButtonAction
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
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
        var latch = CountDownLatch(1)
        var listener = Events.on<InventoryClickEvent> { event ->
            latch.countDown()
        }
        sendSlotClick(player, 42, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, chestplate)

        latch.await()
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
        assertEquals(chestplate, player.equipment[EquipmentSlot.CHESTPLATE])
        assertSlot(player, 42, chestplate)
        Events.unregister(listener)

        latch = CountDownLatch(1)
        listener = Events.on<InventoryClickEvent> { event ->
            latch.countDown()
        }
        sendSlotClick(player, 42, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, chestplate)

        latch.await()
        assertEquals(chestplate, player.inventory.cursorItem.value)
        assertEquals(ItemStack.AIR, player.equipment[EquipmentSlot.CHESTPLATE])
        assertSlot(player, 42, ItemStack.AIR)
        Events.unregister(listener)
    }

    @Test
    fun testClickSwap() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()

        val chestplate1 = ItemStack(Items.NETHERITE_CHESTPLATE)
        val chestplate2 = ItemStack(Items.GOLDEN_CHESTPLATE)

        player.equipment[EquipmentSlot.CHESTPLATE] = chestplate1
        player.inventory.cursorItem.value = chestplate2

        val latch = CountDownLatch(1)
        val listener = Events.on<InventoryClickEvent> { event ->
            latch.countDown()
        }

        sendSlotClick(player, 42, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, chestplate2)
        latch.await()

        assertEquals(chestplate1, player.inventory.cursorItem.value)
        assertEquals(chestplate2, player.equipment[EquipmentSlot.CHESTPLATE])
        Events.unregister(listener)
    }

    @Test
    fun testShiftClicking() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()

        val chestplate = ItemStack(Items.NETHERITE_CHESTPLATE)
        player.inventory[0] = chestplate

        var latch = CountDownLatch(1)
        var listener = Events.on<PlayerEquipEvent> { event ->
            latch.countDown()
        }

        sendSlotClick(player, 0, NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL_SHIFT, chestplate)

        latch.await()
        assertEquals(chestplate, player.equipment[EquipmentSlot.CHESTPLATE])
        assertSlot(player, 0, ItemStack.AIR)

        latch = CountDownLatch(1)
        Events.unregister(listener)
        listener = Events.on<PlayerEquipEvent> { event ->
            latch.countDown()
        }
        sendSlotClick(player, 42, NormalShiftButtonAction.SHIFT_LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL_SHIFT, ItemStack.AIR)

        latch.await()
        Events.unregister(listener)
        listener = Events.on<PlayerEquipEvent> { event ->
            latch.countDown()
        }

        assertEquals(ItemStack.AIR, player.equipment[EquipmentSlot.CHESTPLATE])
        println(player.inventory)
        assertSlot(player, 9, chestplate)
        Events.unregister(listener)
    }
}