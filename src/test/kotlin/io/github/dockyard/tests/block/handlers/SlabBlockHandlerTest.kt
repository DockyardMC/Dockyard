package io.github.dockyard.tests.block.handlers

import io.github.dockyard.tests.PlayerTestUtil
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.inventory.clearInventory
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.Direction
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.utils.vectors.Vector3
import io.github.dockyardmc.utils.vectors.Vector3f
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test

class SlabBlockHandlerTest {

    @BeforeTest
    fun before() {
        TestServer.getOrSetupServer()
        PlayerTestUtil.getOrCreateFakePlayer().clearInventory()
        BlockHandlerTestUtil.reset()
    }

    @Test
    fun testPlace() {
        val world = WorldManager.mainWorld
        PlayerTestUtil.getOrCreateFakePlayer().teleport(Location(10, 10, 10, WorldManager.mainWorld))
        BlockHandlerTestUtil.reset()

        world.setBlock(0, -1, 0, Blocks.STONE)
        world.setBlock(0, 1, 0, Blocks.STONE)

        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "bottom"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.UP, Vector3(0, -1, 0), Vector3f()))
        BlockHandlerTestUtil.reset()
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "top"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.DOWN, Vector3(0, 1, 0), Vector3f()))
        BlockHandlerTestUtil.reset()

        world.setBlock(0, 0, -1, Blocks.STONE)
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "top"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 1f, 0f)))
        BlockHandlerTestUtil.reset()
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "bottom"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 0f, 0f)))

        BlockHandlerTestUtil.reset()
        BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 1f, 0f))
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "double"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 0f, 0f)))

        BlockHandlerTestUtil.reset()
        BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 0f, 0f))
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "double"), BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 1f, 0f)))

        BlockHandlerTestUtil.reset()
        world.setBlock(0, 1, 0, Blocks.AIR)
        BlockHandlerTestUtil.place(Items.RESIN_BRICK_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 0f, 0f))
        assertEquals(Blocks.RESIN_BRICK_SLAB.withBlockStates("type" to "bottom"), BlockHandlerTestUtil.place(Items.CHERRY_SLAB, Direction.SOUTH, Vector3(0, 0, -1), Vector3f(0f, 1f, 0f)))
        assertEquals(Blocks.AIR.toBlock(), WorldManager.mainWorld.getBlock(0, 1, 0))
    }
}