package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerCancelledDiggingEvent
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.protocol.packets.play.serverbound.PlayerAction
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerActionPacket
import io.github.dockyardmc.maths.vectors.Vector3
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerCancelledDiggingEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerCancelledDiggingEvent> {
            count.countDown()
        }

        PlayerTestUtil.sendPacket(ServerboundPlayerActionPacket(
            PlayerAction.CANCELLED_DIGGING,
            Vector3(0),
            Direction.DOWN,
            0
        ))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}