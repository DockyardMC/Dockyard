package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
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

class EntityViewerRemoveEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        val mob = EntityManager.spawnEntity(Parrot(TestServer.testWorld.locationAt(0,0,0)))
        val player = PlayerTestUtil.getOrCreateFakePlayer()

        mob.addViewer(player)

        pool.on<EntityViewerRemoveEvent> {
            count.countDown()
        }

        mob.removeViewer(player)

        assertTrue(count.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        mob.dispose()
    }
}