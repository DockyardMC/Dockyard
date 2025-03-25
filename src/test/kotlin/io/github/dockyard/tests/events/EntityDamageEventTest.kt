package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntityDamageEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.registry.DamageTypes
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class EntityDamageEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val poorThing = Parrot(TestServer.testWorld.locationAt(0,0,0))
        val count = CountDownLatch(1)

        pool.on<EntityDamageEvent> {
            count.countDown()
        }

        poorThing.damage(0.1f, DamageTypes.SPIT)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        poorThing.dispose()
        pool.dispose()
    }
}