package io.github.dockyard.tests.viewable

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.world.WorldManager
import kotlin.test.BeforeTest
import kotlin.test.Test

class ViewRulesTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testViewRules() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val location = WorldManager.mainWorld.defaultSpawnLocation

        val entity = location.world.spawnEntity(Parrot(location))

        entity.addViewRule("test") { testPlayer ->
            testPlayer.experienceLevel.value >= 5
        }

        player.teleport(location)
        player.entityViewSystem.tick()

        assert(!entity.viewers.contains(player))

        player.experienceLevel.value = 10
        player.entityViewSystem.tick()

        assert(entity.viewers.contains(player))

        player.experienceLevel.value = 0
        player.entityViewSystem.tick()

        assert(!entity.viewers.contains(player))

        location.world.despawnEntity(entity)
    }
}