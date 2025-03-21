package io.github.dockyard.tests

import cz.lukynka.prettylog.LogType
import cz.lukynka.prettylog.log
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.registry.registries.AttributeRegistry
import io.github.dockyardmc.registry.registries.BlockRegistry
import io.github.dockyardmc.registry.registries.ChatTypeRegistry
import io.github.dockyardmc.utils.randomInt
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.BeforeTest
import kotlin.test.Test

class RegistryTests {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testQuickRegistryLists() {
        assertDoesNotThrow {
            Blocks.AIR
            BannerPatterns.BORDER
            Biomes.BIRCH_FOREST
            DamageTypes.TRIDENT
            DimensionTypes.OVERWORLD
            EntityTypes.PIGLIN
            Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE
            JukeboxSongs.DISC_OTHERSIDE
            PaintingVariants.AZTEC2
            Particles.PORTAL
            PotionEffects.OOZING
            Sounds.MUSIC_DISC_CREATOR_MUSIC_BOX
            WolfVariants.CHESTNUT
            Blocks.HEAVY_CORE
            Blocks.CREAKING_HEART
            Tags.BIOME_ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT
            Tags.ITEM_STONE_BRICKS
            Attributes.GRAVITY
        }
    }

    @Test
    fun testRegistries() {
        assertDoesNotThrow {
            RegistryManager.registries.forEach { registry ->
                if(registry is ChatTypeRegistry) return@forEach // dockyard does not do chat type stuff
                log("Testing registry ${registry.identifier}", LogType.DEBUG)
                if(registry is BlockRegistry) {
                    val random = registry.protocolIdToBlock.keys.random()
                    registry.getByProtocolId(random)
                } else {
                    registry.getByProtocolId(randomInt(0, registry.getMaxProtocolId()))
                }
            }
        }
    }
}