package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.ItemGroupCooldownEndEvent
import io.github.dockyardmc.events.ItemGroupCooldownStartEvent
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scheduler.runnables.ticks
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(ItemGroupCooldownStartEvent::class, ItemGroupCooldownEndEvent::class)
class ItemGroupCooldownEventsTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val startCount = CountDownLatch(1)
        val endCount = CountDownLatch(1)

        pool.on<ItemGroupCooldownStartEvent> {
            startCount.countDown()
        }
        pool.on<ItemGroupCooldownEndEvent> {
            endCount.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.setCooldown(Items.ENDER_PEARL, 1.ticks)

        assertTrue(startCount.await(5L, TimeUnit.SECONDS), "ItemGroupCooldownStartEvent didn't work")
        assertTrue(endCount.await(5L, TimeUnit.SECONDS), "ItemGroupCooldownEndEvent didn't work")
        pool.dispose()
    }
}