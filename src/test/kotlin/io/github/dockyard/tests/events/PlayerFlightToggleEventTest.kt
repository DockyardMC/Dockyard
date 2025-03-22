package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerFlightToggleEvent
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerAbilitiesPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerFlightToggleEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testToggleFlight() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.gameMode.value = GameMode.CREATIVE
        player.isFlying.value = false

        pool.on<PlayerFlightToggleEvent> {
            if (it.flying) {
                count.countDown()
            }
        }

        PlayerTestUtil.sendPacket(player, ServerboundPlayerAbilitiesPacket(true))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        player.gameMode.value = GameMode.SURVIVAL
    }
}