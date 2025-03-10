package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDamageEvent
import io.github.dockyardmc.registry.DamageTypes
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerDamageEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        pool.on<PlayerDamageEvent> {
            it.cancelled = true
            count.countDown()
        }
        player.damage(0.1f, DamageTypes.SPIT)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}
