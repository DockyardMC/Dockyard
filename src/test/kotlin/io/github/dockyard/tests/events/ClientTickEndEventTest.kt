package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.ClientTickEndEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientTickEndPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ClientTickEndEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<ClientTickEndEvent> {
            count.countDown()
        }

        PlayerTestUtil.sendPacket(ServerboundClientTickEndPacket())

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}