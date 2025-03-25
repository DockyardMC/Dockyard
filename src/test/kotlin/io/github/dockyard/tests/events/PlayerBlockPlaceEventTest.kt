package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerBlockPlaceEvent
import io.github.dockyardmc.events.PlayerFinishPlacingBlockEvent
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.player.systems.GameMode
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemOnBlockPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.maths.vectors.Vector3
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(PlayerBlockPlaceEvent::class, PlayerFinishPlacingBlockEvent::class)
class PlayerBlockPlaceEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val placeCount = CountDownLatch(1)
        val finishCount = CountDownLatch(1)

        pool.on<PlayerBlockPlaceEvent> { placeCount.countDown() }
        pool.on<PlayerFinishPlacingBlockEvent> { finishCount.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.gameMode.value = GameMode.SURVIVAL
        player.setHeldItem(PlayerHand.MAIN_HAND, Items.DIRT.toItemStack(9))

        PlayerTestUtil.sendPacket(
            player,
            ServerboundUseItemOnBlockPacket(
                PlayerHand.MAIN_HAND,
                Vector3(0),
                Direction.UP,
                0f, 0f, 0f,
                false, false, 0
            )
        )

        assertTrue(placeCount.await(5L, TimeUnit.SECONDS))
        assertTrue(finishCount.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        player.clearInventory()
    }
}