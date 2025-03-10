package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.InventoryGiveItemEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.inventory.give
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class InventoryGiveItemEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<InventoryGiveItemEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.give(Items.DEBUG_STICK)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        player.clearInventory()
    }
}