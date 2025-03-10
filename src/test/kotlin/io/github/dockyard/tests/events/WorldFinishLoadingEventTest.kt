package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.WorldFinishLoadingEvent
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.world.WorldManager
import io.github.dockyardmc.world.generators.VoidWorldGenerator
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class WorldFinishLoadingEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<WorldFinishLoadingEvent> {
            count.countDown()
        }

        val world = WorldManager.create("world_finish_loading_event_test", VoidWorldGenerator(Biomes.THE_VOID), DimensionTypes.THE_END)

        assertTrue(count.await(5L, TimeUnit.SECONDS))

        pool.dispose()
        WorldManager.delete(world)
    }
}