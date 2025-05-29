package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerScreenOpenEvent
import io.github.dockyardmc.ui.CookieClickerScreen
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(PlayerScreenOpenEvent::class)
class PlayerScreenOpenEventTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerScreenOpenEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val screen = CookieClickerScreen()

        screen.open(player)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        screen.dispose()
    }
}