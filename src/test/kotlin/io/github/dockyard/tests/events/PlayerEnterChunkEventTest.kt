package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerEnterChunkEvent
import io.github.dockyardmc.world.chunk.ChunkPos
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerEnterChunkEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.teleport(player.world.locationAt(0, 0, 0))

        val location = player.location.add(16, 0, 0)

        pool.on<PlayerEnterChunkEvent> {
            if (it.newChunkPos == ChunkPos.fromLocation(location)) {
                count.countDown()
            }
        }

        player.teleport(location)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()
    }
}