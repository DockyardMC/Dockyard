package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PluginMessageReceivedEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayPluginMessagePacket
import io.netty.buffer.Unpooled
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PluginMessageReceivedEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PluginMessageReceivedEvent> {
            // prevent disasters
            it.cancelled = it.channel == "test"

            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val buffer = Unpooled.buffer(0)
        PlayerTestUtil.sendPacket(player, ServerboundPlayPluginMessagePacket("test", buffer))
        buffer.release()

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}