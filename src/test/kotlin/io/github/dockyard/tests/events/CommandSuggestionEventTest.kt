package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.CommandSuggestionEvent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundCommandSuggestionPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CommandSuggestionEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<CommandSuggestionEvent> {
            count.countDown()
        }

        PlayerTestUtil.sendPacket(ServerboundCommandSuggestionPacket(0, "/hel"))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}