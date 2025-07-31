package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.data.components.ConsumableComponent
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerCancelledConsumingEvent
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundSetPlayerHeldItemPacket
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundUseItemPacket
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scheduler.runLaterAsync
import io.github.dockyardmc.scheduler.runnables.ticks
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@TestFor(PlayerCancelledConsumingEvent::class)
class PlayerCancelConsumingEventTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerCancelledConsumingEvent> {
            count.countDown()
        }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.setHeldItem(PlayerHand.MAIN_HAND, Items.APPLE.toItemStack().withConsumable(1f, ConsumableComponent.Animation.EAT))

        PlayerTestUtil.sendPacket(player, ServerboundUseItemPacket(PlayerHand.MAIN_HAND, 0, 0f, 0f))
        runLaterAsync(5.ticks) {
            PlayerTestUtil.sendPacket(player, ServerboundSetPlayerHeldItemPacket(9))
        }

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        PlayerTestUtil.sendPacket(player, ServerboundSetPlayerHeldItemPacket(0))
    }
}