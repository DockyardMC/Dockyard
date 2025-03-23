package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerRightClickWithItemEvent
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemPacket
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerRightClickWithItemEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerRightClickWithItemEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.setHeldItem(PlayerHand.MAIN_HAND, Items.DEBUG_STICK.toItemStack())

        PlayerTestUtil.sendPacket(
            player, ServerboundUseItemPacket(PlayerHand.MAIN_HAND, 0, 0f, 0f)
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}