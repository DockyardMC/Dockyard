package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalButtonAction
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryClickTest {

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
    fun testBasicLeftClickActions() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        player.inventory[0] = itemStack

        assertEquals(itemStack, player.inventory[0])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)

        assertEquals(ItemStack.AIR, player.inventory[0])
        assertEquals(itemStack, player.inventory.cursorItem.value)

        sendSlotClick(player, 3, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)

        assertEquals(ItemStack.AIR, player.inventory[0])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
        assertEquals(itemStack, player.inventory[3])
    }

    @Test
    fun testLeftClickSwap() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack1 = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        val itemStack2 = ItemStack(Items.FLINT_AND_STEEL).withDisplayName("<yellow>thing that makes tnt go booom")

        player.inventory[0] = itemStack1
        player.inventory[1] = itemStack2

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)
        sendSlotClick(player, 1, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack1)

        assertEquals(ItemStack.AIR, player.inventory[0])
        assertEquals(itemStack1, player.inventory[1])
        assertEquals(itemStack2, player.inventory.cursorItem.value)

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack2)
        assertEquals(itemStack2, player.inventory[0])
        assertEquals(itemStack1, player.inventory[1])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
    }

    @Test
    fun testLeftClickStack() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(3)
        player.inventory[0] = itemStack
        player.inventory.cursorItem.value = itemStack

        assertEquals(itemStack, player.inventory[0])
        assertEquals(itemStack, player.inventory.cursorItem.value)

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)

        assertEquals(itemStack.withAmount(6), player.inventory[0])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
    }

    @Test
    fun testBasicRightClickAction() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        player.inventory[0] = itemStack

        assertEquals(itemStack, player.inventory[0])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)

        sendSlotClick(player, 0, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)

        assertEquals(itemStack.withAmount(3), player.inventory[0])
        assertEquals(itemStack.withAmount(3), player.inventory.cursorItem.value)

        sendSlotClick(player, 3, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)

        assertEquals(itemStack.withAmount(3), player.inventory[0])
        assertEquals(itemStack.withAmount(2), player.inventory.cursorItem.value)
        assertEquals(itemStack.withAmount(1), player.inventory[3])

        sendSlotClick(player, 3, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)

        assertEquals(itemStack.withAmount(1), player.inventory.cursorItem.value)
        assertEquals(itemStack.withAmount(2), player.inventory[3])

        sendSlotClick(player, 3, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)

        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
        assertEquals(itemStack.withAmount(3), player.inventory[3])
    }

    @Test
    fun testRightClickSwap() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack1 = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom")
        val itemStack2 = ItemStack(Items.FLINT_AND_STEEL).withDisplayName("<yellow>thing that makes tnt go booom")

        player.inventory[0] = itemStack1
        player.inventory[1] = itemStack2

        sendSlotClick(player, 0, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, ItemStack.AIR)
        sendSlotClick(player, 1, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack1)

        assertEquals(ItemStack.AIR, player.inventory[0])
        assertEquals(itemStack1, player.inventory[1])
        assertEquals(itemStack2, player.inventory.cursorItem.value)

        sendSlotClick(player, 0, NormalButtonAction.RIGHT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack2)
        assertEquals(itemStack2, player.inventory[0])
        assertEquals(itemStack1, player.inventory[1])
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
    }

    @Test
    fun testLeftClickOutside() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom")
        val countDownLatch = CountDownLatch(1)

        player.inventory.cursorItem.value = itemStack

        var droppedItem: ItemStack = ItemStack.AIR
        val listener = Events.on<PlayerDropItemEvent> {
            droppedItem = it.itemStack
            countDownLatch.countDown()
        }

        sendSlotClick(player, -999, NormalButtonAction.LEFT_CLICK_OUTSIDE_INVENTORY.button, ContainerClickMode.NORMAL, itemStack)

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))
        assertEquals(ItemStack.AIR, player.inventory.cursorItem.value)
        assertEquals(itemStack, droppedItem)

        WorldManager.mainWorld.entities.toList().forEach { entity ->
            WorldManager.mainWorld.despawnEntity(entity)
        }
        Events.unregister(listener)
    }

    @Test
    fun testRightClickOutside() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        val countDownLatch = CountDownLatch(1)

        player.inventory.cursorItem.value = itemStack

        var droppedItem: ItemStack = ItemStack.AIR
        val listener = Events.on<PlayerDropItemEvent> {
            droppedItem = it.itemStack
            countDownLatch.countDown()
        }

        sendSlotClick(player, -999, NormalButtonAction.RIGHT_CLICK_OUTSIDE_INVENTORY.button, ContainerClickMode.NORMAL, itemStack)

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))
        assertEquals(itemStack.withAmount(5), player.inventory.cursorItem.value)
        assertEquals(itemStack.withAmount(1), droppedItem)

        WorldManager.mainWorld.entities.toList().forEach { entity ->
            WorldManager.mainWorld.despawnEntity(entity)
        }
        Events.unregister(listener)
    }
}