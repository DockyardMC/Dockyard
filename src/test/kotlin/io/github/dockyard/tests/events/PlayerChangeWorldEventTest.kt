package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerChangeWorldEvent
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerChangeWorldEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val world = WorldManager.create("playerchangeworldeventtest", VoidWorldGenerator(Biomes.DESERT), DimensionTypes.OVERWORLD)

        pool.on<PlayerChangeWorldEvent> {
            if (it.newWorld == world) {
                count.countDown()
            }
        }
        world.join(player)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
        WorldManager.delete(world)
    }
}