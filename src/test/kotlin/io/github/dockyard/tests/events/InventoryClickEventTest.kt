package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.sendSlotClick
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.InventoryClickEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.protocol.packets.play.serverbound.ContainerClickMode
import io.github.dockyardmc.protocol.packets.play.serverbound.NormalButtonAction
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class InventoryClickEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)
        val itemStack = Items.DANDELION.toItemStack(2).withDisplayName("pepeFlower")

        pool.on<InventoryClickEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.inventory.cursorItem.value = itemStack

        sendSlotClick(player, 0, NormalButtonAction.LEFT_MOUSE_CLICK.button, ContainerClickMode.NORMAL, itemStack)
        assertTrue(count.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        player.clearInventory()
    }
}