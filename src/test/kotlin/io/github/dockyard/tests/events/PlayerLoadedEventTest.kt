package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerLoadedEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerLoadedPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerLoadedEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerLoadedEvent> { count.countDown() }

        PlayerTestUtil.sendPacket(ServerboundPlayerLoadedPacket())

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}