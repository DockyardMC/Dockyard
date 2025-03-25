package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.ServerListPingEvent
import io.github.dockyardmc.protocol.packets.handshake.ServerboundStatusRequestPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ServerListPingEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count  = CountDownLatch(1)

        pool.on<ServerListPingEvent> {count.countDown()}

        PlayerTestUtil.sendPacket(ServerboundStatusRequestPacket())
        assertTrue(count.await(5L, TimeUnit.SECONDS))

        pool.dispose()
    }
}