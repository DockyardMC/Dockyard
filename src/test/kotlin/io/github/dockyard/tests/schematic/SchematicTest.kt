package io.github.dockyard.tests.schematic

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.schematics.SchematicReader
import io.github.dockyardmc.schematics.placeSchematic
import io.github.dockyardmc.utils.Resources
import io.github.dockyardmc.world.WorldManager
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SchematicTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testParsing() {
        val world = WorldManager.mainWorld
        assertDoesNotThrow {
            val schematic = SchematicReader.read(Resources.getFile("test.schem").readBytes())

            world.placeSchematic(schematic, world.locationAt(0, 0, 0))

            assertEquals(Blocks.RED_WOOL, world.locationAt(0, 0, 0).block.registryBlock)
            assertEquals(Blocks.NETHER_BRICK_FENCE, world.locationAt(4, 1, 5).block.registryBlock)
            assertEquals(Blocks.STONE, world.locationAt(27, 1, 23).block.registryBlock)

        }
    }
}