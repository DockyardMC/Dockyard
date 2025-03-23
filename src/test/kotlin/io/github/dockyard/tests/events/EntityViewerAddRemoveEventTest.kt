package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestFor
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.events.EntityViewerAddEvent
import io.github.dockyardmc.events.EntityViewerRemoveEvent
import io.github.dockyardmc.events.EventPool
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test

@TestFor(EntityViewerAddEvent::class, EntityViewerRemoveEvent::class)
class EntityViewerAddRemoveEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val addCount = CountDownLatch(1)
        val removeCount = CountDownLatch(1)

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val mob = EntityManager.spawnEntity(Parrot(player.world.locationAt(0,0,0)))

        pool.on<EntityViewerAddEvent> {
            addCount.countDown()
        }
        pool.on<EntityViewerRemoveEvent> {
            removeCount.countDown()
        }

        mob.addViewer(player)
        assertTrue(addCount.await(5L, TimeUnit.SECONDS))

        mob.removeViewer(player)
        assertTrue(removeCount.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        mob.dispose()
    }
}
