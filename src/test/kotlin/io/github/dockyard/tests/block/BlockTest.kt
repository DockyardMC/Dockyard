package io.github.dockyard.tests.block

import cz.lukynka.prettylog.log
import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.world.block.Block
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.world.chunk.ChunkUtils
import io.github.dockyardmc.utils.CustomDataHolder
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
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

        val block = Blocks.BIRCH_SLAB.toBlock()
        val block2 = Blocks.BIRCH_SLAB.toBlock()
        val block3 = Blocks.BIRCH_SLAB.toBlock().withBlockStates("state" to "top")
        val block4 = Blocks.REDSTONE_BLOCK.toBlock()

        val registryBlock = Blocks.GRASS_BLOCK
        val registryBlock2 = Blocks.GRASS_BLOCK
        val registryBlock3 = Blocks.DIRT

        assertEquals(block, block2)
        log("${block.asString()}, ${block3.asString()}")
        assertNotEquals(block, block3)
        assertNotEquals(block, block4)
        assertEquals(block.withBlockStates("state" to "top"), block3)

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
    fun testCustomData() {
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
        BlockRegistry.getEntries().keyToValue().forEach {
            assertEquals(it.value, BlockRegistry.getByProtocolId(it.value.getLegacyProtocolId()))
        }
    }

    @Test
    fun testInvalidBlockStates() {
        assertDoesNotThrow { Block.getBlockFromStateString("minecraft:oak_slab[type=top,waterlogged=false]") }
        assertThrows<IllegalArgumentException> { Block.getBlockFromStateString("minecraft:oak_slab[silly=true,gay=true]") }
    }

    @Test
    fun testBlockStateParsing() {
        val matches = mutableMapOf<String, Int>(
            "minecraft:coarse_dirt" to 11
        )

        matches.forEach { (identifier, blockStateId) ->
            assertEquals(blockStateId, Block.getBlockFromStateString(identifier).getProtocolId())
        }

        val parsed = Block.parseBlockStateString("minecraft:oak_slab[type=top]")

        assertEquals("minecraft:oak_slab", parsed.first)
        assertEquals(mapOf("type" to "top"), parsed.second)
    }
}