package io.github.dockyard.tests.hashing

import io.github.dockyard.tests.TestServer
import io.github.dockyardmc.attributes.AttributeModifier
import io.github.dockyardmc.attributes.AttributeOperation
import io.github.dockyardmc.attributes.EquipmentSlotGroup
import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.data.CRC32CHasher
import io.github.dockyardmc.data.components.*
import io.github.dockyardmc.maths.vectors.Vector3
import io.github.dockyardmc.protocol.DataComponentHashable
import io.github.dockyardmc.protocol.types.ConsumeEffect
import io.github.dockyardmc.protocol.types.EquipmentSlot
import io.github.dockyardmc.protocol.types.WorldPosition
import io.github.dockyardmc.registry.*
import io.github.dockyardmc.registry.registries.PotionTypeRegistry
import io.github.dockyardmc.registry.registries.SoundRegistry
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.sounds.BuiltinSoundEvent
import io.github.dockyardmc.sounds.CustomSoundEvent
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class HashTest {

    @BeforeTest
    fun prepare() {
        TestServer.getOrSetupServer()
    }

    @Test
    fun testHashing() {
        val expectedHashes = mapOf<DataComponentHashable, Int>(
            UseCooldownComponent(1.6f, "minecraft:test") to 493336604,
            AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE) to -1483981544,
            Modifier(Attributes.ATTACK_SPEED, AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE), EquipmentSlotGroup.ANY) to 1291119738,
            AttributeModifiersComponent(listOf(Modifier(Attributes.ATTACK_SPEED, AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE), EquipmentSlotGroup.ANY))) to 168186938,
            AttributeModifiersComponent(listOf()) to -1978007022,
            CustomSoundEvent(Sounds.BLOCK_NOTE_BLOCK_BANJO, null) to 2036171673,
            BuiltinSoundEvent(Sounds.BLOCK_NOTE_BLOCK_BANJO, SoundRegistry[Sounds.BLOCK_NOTE_BLOCK_BANJO]) to 952047800,
            ConsumableComponent(1.6f, ConsumableComponent.Animation.EAT, Sounds.ITEM_GOAT_HORN_SOUND_0, true, listOf()) to -102904843,
            ConsumableComponent(0.3f, ConsumableComponent.Animation.BLOCK, Sounds.ITEM_BOOK_PUT, false, listOf()) to -1085734156,
            AppliedPotionEffect(PotionEffects.LUCK, AppliedPotionEffectSettings(1, 5.seconds, true, false, true, null)) to -1904986478,
            ConsumeEffect.ApplyEffects(listOf(AppliedPotionEffect(PotionEffects.LUCK, AppliedPotionEffectSettings(1, 5.seconds, true, false, true, null))), 1.0f) to -181115074,
            ConsumeEffect.RemoveEffects(listOf(PotionEffects.LUCK, PotionEffects.HASTE)) to -1438921926,
            ConsumeEffect.ClearAllEffects() to -982207288,
            ConsumeEffect.PlaySound(Sounds.ITEM_GOAT_HORN_SOUND_0) to -102904843,
            ConsumeEffect.TeleportRandomly() to -982207288,
            ConsumeEffect.TeleportRandomly(5.0f) to 984729518,
            CustomModelDataComponent(listOf(1f, 2f), listOf(true, false), listOf("gay", "month"), listOf(CustomColor(1, 1, 1))) to 766388248,
            EquippableComponent(EquipmentSlot.CHESTPLATE, Sounds.ITEM_GOAT_HORN_SOUND_0, "minecraft:dockyard", null, null, true, true, false, false) to 888528263,
            FoodComponent(2, 1.3f, true) to 474066665,
            ItemBlockStateComponent(mapOf("month?" to "gay!")) to 725075553,
            LodestoneTrackerComponent(WorldPosition("minecraft:main", Vector3(1, 2, 3)), true) to 728072173,
            MapDecorationsComponent(mapOf("test" to MapDecorationsComponent.Decoration("test", 1.0, 2.0, 3f))) to -1461889578,
            PotionContentsComponent(PotionTypeRegistry["minecraft:awkward"], CustomColor(1, 1, 1), listOf(), "test") to 484392761,
            ProfileComponent("LukynkaCZE", UUID.fromString("0c9151e4-7083-418d-a29c-bbc58f7c741b"), listOf()) to 1731625998,
            SuspiciousStewEffectsComponent(
                listOf(
                    SuspiciousStewEffectsComponent.Effect(PotionEffects.LUCK, 5.seconds),
                    SuspiciousStewEffectsComponent.Effect(PotionEffects.BAD_LUCK, 8.seconds),
                )
            ) to 72427758
        )

        expectedHashes.forEach { (hashable, hash) ->
            assertEquals(-340533995, CRC32CHasher.ofColor(CustomColor(1, 1, 1)))
            assertEquals(hash, hashable.hashStruct().getHashed())
        }
    }
}