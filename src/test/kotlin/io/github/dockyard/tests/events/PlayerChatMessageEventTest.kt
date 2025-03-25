package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerChatMessageEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerChatMessagePacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerChatMessageEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerChatMessageEvent> {
            count.countDown()
        }

        PlayerTestUtil.sendPacket(ServerboundPlayerChatMessagePacket("I <3 Dockyard"))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}