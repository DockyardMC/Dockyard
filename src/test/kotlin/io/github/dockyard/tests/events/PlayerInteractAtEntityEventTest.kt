package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerInteractAtEntityEvent
import io.github.dockyardmc.player.PlayerHand
import io.github.dockyardmc.protocol.packets.play.serverbound.EntityInteractionType
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundEntityInteractPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerInteractAtEntityEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerInteractAtEntityEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val entity = EntityManager.spawnEntity(Parrot(player.location.add(1, 2, 0)))

        PlayerTestUtil.sendPacket(
            player,
            ServerboundEntityInteractPacket(
                entity, EntityInteractionType.INTERACT_AT,
                0f, 0f, 0f,
                PlayerHand.MAIN_HAND, sneaking = false
            )
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        entity.dispose()
    }
}