package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.HorseJumpEvent
import io.github.dockyardmc.player.PlayerAction
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerCommandPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class HorseJumpEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<HorseJumpEvent> {
            count.countDown()
        }
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        PlayerTestUtil.sendPacket(
            player,
            ServerboundPlayerCommandPacket(player.id, PlayerAction.HORSE_JUMP_START)
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}