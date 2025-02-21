package io.github.dockyard.tests.bound

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.bounds.Bound
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerEnterBoundEvent
import io.github.dockyardmc.world.WorldManager
import java.util.concurrent.CountDownLatch
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BoundTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testPlayerEnterLeave() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val world = WorldManager.mainWorld

        player.teleport(world.locationAt(-5, -5, -5))

        var latch = CountDownLatch(1)
        val bound = Bound(world.locationAt(0, 0, 0), world.locationAt(10, 10, 10))

        bound.onEnter {
            latch.countDown()
        }

        player.teleport(world.locationAt(5, 5, 5))

        latch.await()
        assertContains(bound.players, player)
        assertContains(bound.getEntities(), player)

        latch = CountDownLatch(1)
        bound.onLeave {
            latch.countDown()
        }

        player.teleport(world.locationAt(-5, -5, -5))

        latch.await()
        assertEquals(false, bound.players.contains(player))
        assertEquals(false, bound.getEntities().contains(player))
        bound.dispose()
    }

    @Test
    fun testCreationWithPlayerInside() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val world = WorldManager.mainWorld
        val latch = CountDownLatch(1)
        val pool = EventPool()

        player.teleport(world.locationAt(5, 5, 5))

        pool.on<PlayerEnterBoundEvent> { event ->
            latch.countDown()
        }

        val bound = Bound(world.locationAt(0, 0, 0), world.locationAt(10, 10, 10))

        latch.await()
        assertContains(bound.players, player)

        pool.dispose()
        bound.dispose()
    }

    @Test
    fun testResize() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val world = WorldManager.mainWorld
        val latch = CountDownLatch(1)
        val pool = EventPool()

        player.teleport(world.locationAt(7, 7, 7))

        pool.on<PlayerEnterBoundEvent> { event ->
            latch.countDown()
        }

        val bound = Bound(world.locationAt(0, 0, 0), world.locationAt(5, 5, 5))
        assertEquals(false, bound.players.contains(player))

        bound.resize(world.locationAt(0, 0, 0), world.locationAt(10, 10, 10))

        latch.await()
        assertContains(bound.players, player)

        pool.dispose()
        bound.dispose()
    }
}