package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntitySpawnEvent
import io.github.dockyardmc.events.EventPool
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EntitySpawnEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<EntitySpawnEvent> {
            count.countDown()
        }
        val entity = TestServer.testWorld.spawnEntity(Parrot(TestServer.testWorld.locationAt(0, 0, 0)))

        assertTrue(count.await(5L, TimeUnit.SECONDS))

        entity.dispose()
        pool.dispose()
    }
}