package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.ItemDropEntity
import io.github.dockyardmc.events.EntityPickupItemEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EntityPickupItemEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<EntityPickupItemEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        EntityManager.spawnEntity(ItemDropEntity(player.location, Items.MUD.toItemStack()))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        player.clearInventory()
    }
}