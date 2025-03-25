package io.github.dockyard.tests.entity

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.entity.EntityManager.despawnEntity
import io.github.dockyardmc.entity.EntityManager.spawnEntity
import io.github.dockyardmc.entity.Parrot
import io.github.dockyardmc.world.WorldManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EntityViewSystemTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
        PlayerTestUtil.getOrCreateFakePlayer().entityViewSystem.clear()
    }

    @Test
    fun testViewSystem() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val entity = player.world.spawnEntity(Parrot(player.location.add(64, 0, 0))) as Parrot
        entity.autoViewable = true

        player.entityViewSystem.tick()

        assertEquals(true, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(1, player.entityViewSystem.visibleEntities.size)

        player.world.despawnEntity(entity)

        player.entityViewSystem.tick()

        assertEquals(false, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(0, player.entityViewSystem.visibleEntities.size)
    }

    @Test
    fun testViewSystemCustomRange() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val entity = player.world.spawnEntity(Parrot(player.location.add(65, 0, 0))) as Parrot
        entity.autoViewable = true
        entity.viewDistanceBlocks = 65

        player.entityViewSystem.tick()

        assertEquals(true, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(1, player.entityViewSystem.visibleEntities.size)

        player.world.despawnEntity(entity)

        entity.teleport(entity.location.add(1, 0, 0))

        player.entityViewSystem.tick()

        assertEquals(false, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(0, player.entityViewSystem.visibleEntities.size)
        player.world.despawnEntity(entity)
    }

    @Test
    fun testViewSystemOutOfViewDistance() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val entity = player.world.spawnEntity(Parrot(player.location.add(65, 0, 0))) as Parrot
        entity.autoViewable = true

        player.entityViewSystem.tick()

        assertEquals(false, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(0, player.entityViewSystem.visibleEntities.size)

        entity.teleport(entity.location.add(-2, 0, 0))

        player.entityViewSystem.tick()

        assertEquals(true, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(1, player.entityViewSystem.visibleEntities.size)

        player.world.despawnEntity(entity)
    }

    @Test
    fun testAutoViewable() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        player.teleport(WorldManager.mainWorld.defaultSpawnLocation)
        val entity = player.world.spawnEntity(Parrot(player.location.add(64, 0, 0))) as Parrot
        entity.autoViewable = false

        player.entityViewSystem.tick()

        assertEquals(false, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(0, player.entityViewSystem.visibleEntities.size)

        entity.autoViewable = true

        player.entityViewSystem.tick()

        assertEquals(true, player.entityViewSystem.visibleEntities.contains(entity))
        assertEquals(1, player.entityViewSystem.visibleEntities.size)
        player.world.despawnEntity(entity)
    }

    @Test
    fun testWorldSwitch() {
        val player = PlayerTestUtil.getOrCreateFakePlayer()
        val world1Parrot = player.world.spawnEntity(Parrot(player.location.add(64, 0, 0))) as Parrot
        val world2Parrot = player.world.spawnEntity(Parrot(TestServer.testWorld.defaultSpawnLocation.add(64, 0, 0))) as Parrot
        world1Parrot.autoViewable = true
        world2Parrot.autoViewable = true

        player.entityViewSystem.tick()

        assertEquals(true, player.entityViewSystem.visibleEntities.contains(world1Parrot))
        assertEquals(true, world1Parrot.viewers.contains(player))
        assertEquals(false, player.entityViewSystem.visibleEntities.contains(world2Parrot))
        assertEquals(false, world2Parrot.viewers.contains(player))
        log(player.entityViewSystem.visibleEntities.map { it::class.simpleName }.toString())
        assertEquals(1, player.entityViewSystem.visibleEntities.size)

        player.teleport(TestServer.testWorld.defaultSpawnLocation)

        assertEquals(false, player.entityViewSystem.visibleEntities.contains(world1Parrot))
        assertEquals(false, world1Parrot.viewers.contains(player))
        assertEquals(true, player.entityViewSystem.visibleEntities.contains(world2Parrot))
        assertEquals(true, world2Parrot.viewers.contains(player))
        assertEquals(1, player.entityViewSystem.visibleEntities.size)

        player.world.despawnEntity(world1Parrot)
        player.world.despawnEntity(world2Parrot)
        player.teleport(WorldManager.mainWorld.defaultSpawnLocation)
    }
}