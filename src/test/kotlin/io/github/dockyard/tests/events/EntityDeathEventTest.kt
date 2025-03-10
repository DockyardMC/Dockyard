package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntityDeathEvent
import io.github.dockyardmc.events.EventPool
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EntityDeathEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<EntityDeathEvent> {
            count.countDown()
        }

        val entity = Parrot(TestServer.testWorld.locationAt(0,0,0))
        entity.kill()

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        entity.dispose()
    }
}