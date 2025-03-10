package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.Squid
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerSteerVehicleEvent
import io.github.dockyardmc.location.Point
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundMoveVehiclePacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerSteerVehicleEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val vehicle = EntityManager.spawnEntity(Squid(player.location))
        vehicle.passengers.add(player)

        pool.on<PlayerSteerVehicleEvent> {
            if (it.vehicle == vehicle && it.player == player) {
                count.countDown()
            }
        }

        PlayerTestUtil.sendPacket(ServerboundMoveVehiclePacket(Point(0.0, 0.0, 0.0, 0f, 0f), true))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        vehicle.dispose()
    }
}