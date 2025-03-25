package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.ServerTickEvent
import io.github.dockyardmc.events.ServerTickMonitorEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(ServerTickEvent::class, ServerTickMonitorEvent::class)
class ServerTickEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)
        val monitorCount = CountDownLatch(1)

        // TestServer.server is already ticking
        pool.on<ServerTickEvent> {
            count.countDown()
        }
        pool.on<ServerTickMonitorEvent> {
            monitorCount.countDown()
        }

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        assertTrue(monitorCount.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}