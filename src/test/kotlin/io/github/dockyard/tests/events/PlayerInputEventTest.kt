package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerInputEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundClientInputPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(PlayerInputEvent::class)
class PlayerInputEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(2)

        val player = PlayerTestUtil.getOrCreateFakePlayer()

        pool.on<PlayerInputEvent> {
            count.countDown()
        }

        PlayerTestUtil.sendPacket(
            player,
            ServerboundClientInputPacket(
                forward = true,
                backward = false,
                left = false,
                right = false,
                jump = false,
                shift = false,
                sprint = false
            )
        )
        PlayerTestUtil.sendPacket(
            player,
            ServerboundClientInputPacket(
                forward = false,
                backward = false,
                left = false,
                right = false,
                jump = false,
                shift = false,
                sprint = false
            )
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}