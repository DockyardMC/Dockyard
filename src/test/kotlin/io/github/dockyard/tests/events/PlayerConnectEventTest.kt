package io.github.dockyard.tests.events

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager
import io.github.dockyardmc.events.EventPool
import io.github.dockyardmc.events.PlayerConnectEvent
import io.github.dockyardmc.player.Player
import io.github.dockyardmc.player.PlayerManager
import io.github.dockyardmc.protocol.PlayerNetworkManager
import io.github.dockyardmc.world.WorldManager
import io.netty.channel.ChannelHandlerContext
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PlayerConnectEventTest {
    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testEventFires() {
        val pool = EventPool()
        val count = CountDownLatch(1)

        pool.on<PlayerConnectEvent> { count.countDown() }

        val player = Player(
            "LukynkaCZE_2",
            id = EntityManager.entityIdCounter.incrementAndGet(),
            uuid = UUID.randomUUID(),
            world = WorldManager.mainWorld,
            connection = Mockito.mock<ChannelHandlerContext>(),
            address = "0.0.0.0",
            networkManager = PlayerNetworkManager(),
        )
        PlayerManager.add(player, player.networkManager)
        player.world.join(player)

        assertTrue(count.await(5L, TimeUnit.SECONDS))
        pool.dispose()

        PlayerManager.remove(player)
    }
}