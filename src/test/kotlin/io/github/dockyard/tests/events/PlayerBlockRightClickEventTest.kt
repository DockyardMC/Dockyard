package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerBlockRightClickEvent
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemOnBlockPacket
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerBlockRightClickEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerBlockRightClickEvent> {
            count.countDown()
        }
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.mainHandItem = Items.APPLE.toItemStack()

        PlayerTestUtil.sendPacket(
            player,
            ServerboundUseItemOnBlockPacket(
                PlayerHand.MAIN_HAND,
                player.location.toVector3(),
                Direction.UP,
                cursorX = 0f,
                cursorY = 0f,
                cursorZ = 0f,
                insideBlock = false,
                hitWorldBorder = false,
                sequence = 0, 
            )
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        player.inventory.clear()
    }
}
