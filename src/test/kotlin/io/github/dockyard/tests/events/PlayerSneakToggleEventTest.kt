package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerSneakToggleEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientInputPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerSneakToggleEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(2)

        pool.on<PlayerSneakToggleEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        PlayerTestUtil.sendPacket(player, ServerboundClientInputPacket(false, false, false, false, false, true, false))
        PlayerTestUtil.sendPacket(player, ServerboundClientInputPacket(false, false, false, false, false, false, false))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}