package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerSprintToggleEvent
import io.github.dockyardmc.player.PlayerAction
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundPlayerCommandPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerSprintToggleEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(2)

        pool.on<PlayerSprintToggleEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        PlayerTestUtil.sendPacket(player, ServerboundPlayerCommandPacket(player.id, PlayerAction.SPRINTING_START))
        PlayerTestUtil.sendPacket(player, ServerboundPlayerCommandPacket(player.id, PlayerAction.SPRINTING_END))

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}