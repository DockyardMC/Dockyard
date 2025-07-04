package io.github.dockyard.tests.inventory

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.assertSlot
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.events.Events
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.item.ItemStack
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.DropButtonAction
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryDropTest {

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
    fun testDropItem() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        player.inventory[0] = itemStack
        val countDownLatch = CountDownLatch(1)

        var droppedItem = ItemStack.AIR
        val listener = Events.on<PlayerDropItemEvent> { event ->
            droppedItem = event.itemStack
            countDownLatch.countDown()
        }

        sendSlotClick(player, 0, DropButtonAction.DROP.button, ContainerClickMode.DROP, itemStack)

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))
        assertSlot(player, 0, itemStack.withAmount(5))
        assertEquals(itemStack.withAmount(1), droppedItem)

        Events.unregister(listener)
        WorldManager.mainWorld.entities.toList().forEach { entity ->
            WorldManager.mainWorld.despawnEntity(entity)
        }
    }

    @Test
    fun testControlDropItem() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.clearInventory()
        val itemStack = ItemStack(Items.TNT).withDisplayName("<red>tnt go booom").withAmount(6)
        player.inventory[0] = itemStack
        val countDownLatch = CountDownLatch(1)

        var droppedItem = ItemStack.AIR
        val listener = Events.on<PlayerDropItemEvent> { event ->
            droppedItem = event.itemStack
            countDownLatch.countDown()
        }

        sendSlotClick(player, 0, DropButtonAction.CONTROL_DROP.button, ContainerClickMode.DROP, itemStack)

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS))
        assertSlot(player, 0, ItemStack.AIR)
        assertEquals(itemStack, droppedItem)

        Events.unregister(listener)
        WorldManager.mainWorld.entities.toList().forEach { entity ->
            WorldManager.mainWorld.despawnEntity(entity)
        }
    }
}