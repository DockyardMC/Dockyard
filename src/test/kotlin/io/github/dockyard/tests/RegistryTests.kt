package io.github.dockyard.tests

import io.github.dockyardmc.registry.BannerPatterns
import io.github.dockyardmc.registry.Biomes
import io.github.dockyardmc.registry.Blocks
import io.github.dockyardmc.registry.DamageTypes
import io.github.dockyardmc.registry.DimensionTypes
import io.github.dockyardmc.registry.EntityTypes
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.registry.JukeboxSongs
import io.github.dockyardmc.registry.PaintingVariants
import io.github.dockyardmc.registry.Particles
import io.github.dockyardmc.registry.PotionEffects
import io.github.dockyardmc.registry.RegistryManager
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.WolfVariants
import io.github.dockyardmc.registry.registries.ChatTypeRegistry
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class RegistryTests {

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
        }
    }

    @Test
    fun testRegistries() {
        assertDoesNotThrow {
            RegistryManager.dynamicRegistries.forEach { registry ->
                if(registry is ChatTypeRegistry) return@forEach // dockyard does not do chat type stuff
                registry.getByProtocolId(0)
            }
        }
    }
}