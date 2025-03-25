package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDamageEntityEvent
import io.github.dockyardmc.protocol.packets.play.serverbound.EntityInteractionType
import io.github.dockyardmc.protocol.packets.play.serverbound.ServerboundEntityInteractPacket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerDamageEntityEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerDamageEntityEvent> { count.countDown() }

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val entity = EntityManager.spawnEntity(Parrot(player.location.add(1, 2, 0)))

        PlayerTestUtil.sendPacket(
            player,
            ServerboundEntityInteractPacket(
                entity,
                EntityInteractionType.ATTACK,
                sneaking = false
            )
        )

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        entity.dispose()
    }
}