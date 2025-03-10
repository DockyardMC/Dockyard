package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerMoveEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundSetPlayerPositionPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerMoveEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerMoveEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val vector = player.location.add(1,0,0).toVector3d()

        PlayerTestUtil.sendPacket(player, ServerboundSetPlayerPositionPacket(vector, isOnGround = true, horizontalCollision = false))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}