package io.github.dockyard.tests.block

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.blocks.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.utils.ChunkUtils
import io.github.dockyardmc.utils.CustomDataHolder
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BlockTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @AfterTest
    fun cleanup() {
        WorldManager.mainWorld.setBlock(0, 0, 0, Block.AIR)
    }

    @Test
    fun testEquality() {

        val block = Blocks.AMETHYST_BLOCK.toBlock()
        val block2 = Blocks.AMETHYST_BLOCK.toBlock()
        val block3 = Blocks.AMETHYST_BLOCK.toBlock().withBlockStates("test" to "false")
        val block4 = Blocks.REDSTONE_BLOCK.toBlock()

        val registryBlock = Blocks.GRASS_BLOCK
        val registryBlock2 = Blocks.GRASS_BLOCK
        val registryBlock3 = Blocks.DIRT

        assertEquals(block, block2)
        assertNotEquals(block, block3)
        assertNotEquals(block, block4)
        assertEquals(block.withBlockStates("test" to "false"), block3)

        assertEquals(registryBlock, registryBlock2)
        assertNotEquals(registryBlock, registryBlock3)
    }

    @Test
    fun testTileEntities() {
        val block = Blocks.CHEST

        assertTrue(block.isBlockEntity)
        assertEquals(1, block.blockEntityId)

        WorldManager.mainWorld.setBlock(0, 0, 0, block)

        val index = ChunkUtils.chunkBlockIndex(0, 0, 0)
        val chunk = WorldManager.mainWorld.getChunk(0, 0)

        assertNotNull(chunk)
        assertContains(chunk.blockEntities, index)
    }

    @Test
    fun testProperties() {
        val holder = CustomDataHolder()
        val block = Blocks.HAY_BLOCK.withCustomData(holder)
        holder["test"] = true
        holder["bitches"] = 0
        holder["waifus"] = 10f
        holder["husband"] = "kinich"

        WorldManager.mainWorld.setBlock(0, 0, 0, block)

        val worldBlockData = WorldManager.mainWorld.getBlock(0, 0, 0).customData
        assertEquals(holder, worldBlockData)
        assertEquals(holder.get<Boolean>("test"), worldBlockData!!["test"])
        assertEquals(holder.get<Int>("bitches"), worldBlockData["bitches"])
        assertEquals(holder.get<Float>("waifus"), worldBlockData["waifus"])
        assertEquals(holder.get<String>("husband"), worldBlockData["husband"])
    }

    @Test
    fun testIdConversion() {
        BlockRegistry.blocks.forEach {
            assertEquals(it.value, BlockRegistry.getByProtocolId(it.value.getProtocolId()))
        }
    }

    @Test
    fun testBlockStateParsing() {
        val parsed = Block.parseBlockStateString("minecraft:oak_slab[part=top,silly=true]")

        assertEquals("minecraft:oak_slab", parsed.first)
        assertEquals(mapOf("part" to "top", "silly" to "true"), parsed.second)
    }
}