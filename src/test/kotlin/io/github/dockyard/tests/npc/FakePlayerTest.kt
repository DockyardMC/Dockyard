package io.github.dockyard.tests.npc

import io.github.dockyard.tests.TestServer
import io.github.dockyard.tests.utils.waitUntilFuture
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.npc.FakePlayer
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class FakePlayerTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testSkin() {
        val fakePlayer = WorldManager.mainWorld.spawnEntity<FakePlayer>(FakePlayer(WorldManager.mainWorld.defaultSpawnLocation))

        assertEquals(true, fakePlayer.gameProfile.properties.isEmpty())

        waitUntilFuture(fakePlayer.setSkinFromUsername("LukynkaCZE"))

        assertEquals(false, fakePlayer.gameProfile.properties.isEmpty())
        assertNotNull(fakePlayer.gameProfile.properties.firstOrNull { property -> property.name == "textures" })

        fakePlayer.skin.value = null

        assertEquals(true, fakePlayer.gameProfile.properties.isEmpty())
    }
}