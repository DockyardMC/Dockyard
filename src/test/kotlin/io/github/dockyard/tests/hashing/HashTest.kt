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
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.IntBinaryTag
import net.kyori.adventure.nbt.ListBinaryTag
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
        val byteArray = byteArrayOf(0x48, 0x65, 0x6C, 0x6C, 0x6F)

        val innerCompound = CompoundBinaryTag.builder()
            .putBoolean("gay", true)
            .putByte("testing", 1.toByte())
            .putString("id", "minecraft:estrogen_potion")
            .putByteArray("bytes", byteArray)
            .putDouble("gay_factor", 3.3)
            .putFloat("gay_offset", 5f)
            .build()

        val compound = CompoundBinaryTag.builder()
            .put("test", IntBinaryTag.intBinaryTag(5))
            .putBoolean("gay", true)
            .putString("maya", "lukynka")
            .put("list", ListBinaryTag.from(listOf()))
            .putIntArray("yas", intArrayOf(1, 2, 3, 4))
            .put(innerCompound)
            .put("named", innerCompound)
            .build()

        val expectedHashes = mapOf<DataComponentHashable, Int>(
            UseCooldownComponent(1.6f, "minecraft:test") to 493336604,
            AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE) to -1483981544,
            Modifier(Attributes.ATTACK_SPEED, AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE), EquipmentSlotGroup.ANY, Modifier.Display.Hidden.INSTANCE) to 1210832950,
            AttributeModifiersComponent(listOf(Modifier(Attributes.ATTACK_SPEED, AttributeModifier("minecraft:test", 6.9, AttributeOperation.ADD_VALUE), EquipmentSlotGroup.ANY, Modifier.Display.Default.INSTANCE))) to 168186938,
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
            ) to 72427758,
            BeesComponent(listOf(BeesComponent.Bee(compound, 6, 9))) to -1245536178
        )

        expectedHashes.forEach { (hashable, hash) ->
            assertEquals(-340533995, CRC32CHasher.ofColor(CustomColor(1, 1, 1)))
            assertEquals(hash, hashable.hashStruct().getHashed())
        }
    }
}