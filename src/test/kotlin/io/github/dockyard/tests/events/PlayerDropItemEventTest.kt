package io.github.dockyard.tests.events

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerDropItemEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.registry.Items
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerDropItemEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.inventory[0] = Items.DIAMOND.toItemStack()

        pool.on<PlayerDropItemEvent> {
            count.countDown()
        }

        player.inventory.drop(player.inventory[0])

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        player.world.entities.forEach {
            if(it !is Player) {
                player.world.despawnEntity(it)
            }
        }
    }
}