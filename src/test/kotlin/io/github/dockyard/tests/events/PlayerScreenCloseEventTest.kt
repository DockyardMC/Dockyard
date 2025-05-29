package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerScreenCloseEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCloseContainerPacket
import io.github.dockyardmc.ui.CookieClickerScreen
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(PlayerScreenCloseEvent::class)
class PlayerScreenCloseEventTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerScreenCloseEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val screen = CookieClickerScreen()

        screen.open(player)
        PlayerTestUtil.sendPacket(player, ServerboundCloseContainerPacket(1))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        screen.dispose()
    }
}