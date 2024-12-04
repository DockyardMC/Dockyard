package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.readModifierList
import io.github.dockyardmc.blocks.BlockPredicate
import io.github.dockyardmc.blocks.readBlockPredicate
import io.github.dockyardmc.blocks.readBlockSet
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.player.readProfilePropertyMap
import io.github.dockyardmc.registry.registries.DamageTypeRegistry
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.TrimMaterialRegistry
import io.github.dockyardmc.registry.registries.TrimPatternRegistry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.sounds.readSoundEvent
import io.github.dockyardmc.world.WorldManager
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTString
import java.util.*

fun ByteBuf.readComponent(id: Int): ItemComponent {
    val component = ItemComponents.components[id]
    return when (component) {
        CustomDataItemComponent::class -> CustomDataItemComponent(this.readNBT() as NBTCompound)
        MaxStackSizeItemComponent::class -> MaxStackSizeItemComponent(this.readVarInt())
        MaxDamageItemComponent::class -> MaxDamageItemComponent(this.readVarInt())
        DamageItemComponent::class -> DamageItemComponent(this.readVarInt())
        UnbreakableItemComponent::class -> UnbreakableItemComponent(this.readBoolean())
        CustomNameItemComponent::class -> CustomNameItemComponent((this.readNBT() as NBTCompound).toComponent())
        ItemNameItemComponent::class -> ItemNameItemComponent((this.readNBT() as NBTCompound).toComponent())
        ItemModelItemComponent::class -> ItemModelItemComponent(this.readString())
        LoreItemComponent::class -> {
            val size = this.readVarInt()
            val lines = mutableListOf<Component>()
            for (i in 0 until size) {
                val nbt = this.readNBT()
                lines.add(if (nbt is NBTString) nbt.value.toComponent() else (nbt as NBTCompound).toComponent())
            }
            return LoreItemComponent(lines)
        }

        RarityItemComponent::class -> RarityItemComponent(this.readVarIntEnum<ItemRarity>())
        EnchantmentsItemComponent::class -> {
            val size = this.readVarInt()
            for (i in 0 until size) {
                this.readVarInt()
                this.readVarInt()
            }
            this.readBoolean()
            return EnchantmentsItemComponent()
        }

        CanBePlacedOnItemComponent::class -> {
            val size = this.readVarInt()
            val predicates = mutableListOf<BlockPredicate>()
            for (i in 0 until size) {
                predicates.add(this.readBlockPredicate())
            }
            val showInTooltip = this.readBoolean()

            return CanBePlacedOnItemComponent(predicates, showInTooltip)
        }

        CanBreakItemComponent::class -> {
            val size = this.readVarInt()
            val predicates = mutableListOf<BlockPredicate>()
            for (i in 0 until size) {
                predicates.add(this.readBlockPredicate())
            }
            val showInTooltip = this.readBoolean()

            return CanBreakItemComponent(predicates, showInTooltip)
        }

        AttributeModifiersItemComponent::class -> this.readModifierList()
        CustomModelDataItemComponent::class -> CustomModelDataItemComponent(this.readVarInt())
        HideAdditionalTooltipItemComponent::class -> HideAdditionalTooltipItemComponent()
        HideTooltipItemComponent::class -> HideTooltipItemComponent()
        RepairCostItemComponent::class -> RepairCostItemComponent(this.readVarInt())
        CreativeSlotLockItemComponent::class -> CreativeSlotLockItemComponent()
        EnchantmentGlintOverrideItemComponent::class -> EnchantmentGlintOverrideItemComponent(this.readVarInt() == 1)
        IntangibleProjectileItemComponent::class -> IntangibleProjectileItemComponent()
        FoodItemComponent::class -> FoodItemComponent(this.readVarInt(), this.readFloat(), this.readBoolean())
        ConsumableItemComponent::class -> ConsumableItemComponent(
            this.readFloat(),
            this.readVarIntEnum<ConsumableAnimation>(),
//            this.readOptionalOrDefault<Sound>(Sound(Sounds.ENTITY_GENERIC_EAT)),
            Sound(this.readSoundEvent()),
            this.readBoolean(),
            this.readConsumeEffects()
        )

        UseRemainderItemComponent::class -> UseRemainderItemComponent(this.readItemStack())
        UseCooldownItemComponent::class -> {
            val cooldown = this.readFloat()
            val group = this.readOptionalOrDefault("minecraft:all")
            return UseCooldownItemComponent(cooldown, group)
        }

        DamageResistantItemComponent::class -> DamageResistantItemComponent(DamageTypeRegistry[this.readString()])
        ToolItemComponent::class -> {
            val rules = mutableListOf<ToolRule>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                val blocks = this.readBlockSet()
                val speed = if (this.readBoolean()) this.readFloat() else null
                val correctDropForBlocks = if (this.readBoolean()) this.readBoolean() else false
                rules.add(ToolRule(listOf(), speed, correctDropForBlocks))
            }
            val defaultMiningSpeed = this.readFloat()
            val damagePerBlock = this.readVarInt()
            ToolItemComponent(rules, defaultMiningSpeed, damagePerBlock)
        }

        EnchantableItemComponent::class -> EnchantableItemComponent(this.readVarInt())
        EquippableItemComponent::class -> {
            val slot = this.readVarIntEnum<EquipmentSlot>()
            val sound = Sound(this.readSoundEvent())
            val model = this.readOptionalOrDefault<String>("minecraft:item")
            val cameraOverlay = this.readOptionalOrDefault<String>("minecraft:pumpkin_blur")
            val entityType = this.readEntityTypes()
            val dispensable = this.readBoolean()
            val swappable = this.readBoolean()
            val damageOnHurt = this.readBoolean()

            return EquippableItemComponent(
                slot,
                sound,
                model,
                cameraOverlay,
                entityType,
                dispensable,
                swappable,
                damageOnHurt
            )
        }

        RepairableItemComponent::class -> RepairableItemComponent(this.readRepairable())
        GliderItemComponent::class -> GliderItemComponent()
        TooltipStyleItemComponent::class -> TooltipStyleItemComponent(this.readString())
        DeathProtectionItemComponent::class -> DeathProtectionItemComponent(readConsumeEffects())
        StoredEnchantments::class -> StoredEnchantments(this.readStringList(), this.readBoolean())
        DyedColorItemComponent::class -> DyedColorItemComponent(
            CustomColor.fromRGBInt(this.readInt()),
            this.readBoolean()
        )

        MapColorItemComponent::class -> MapColorItemComponent(CustomColor.fromRGBInt(this.readInt()))
        MapIdItemComponent::class -> MapIdItemComponent(this.readVarInt())
        MapDecorationsItemComponent::class -> MapDecorationsItemComponent(this.readNBT() as NBTCompound)
        MapPostProcessingItemComponent::class -> MapPostProcessingItemComponent(this.readVarIntEnum<MapPostProcessing>())
        ChargedProjectilesItemComponent::class -> ChargedProjectilesItemComponent(this.readItemStackList())
        BundleContentsItemComponent::class -> BundleContentsItemComponent(this.readItemStackList())
        PotionContentsItemComponent::class -> {
            val potion = this.readOptionalOrNull<PotionEffect>()
            val color = this.readOptionalOrNull<CustomColor>()
            val effects = this.readAppliedPotionEffectsList()
            val customName = this.readOptionalOrNull<String>()

            return PotionContentsItemComponent(potion, color, effects, customName)
        }

        SuspiciousStewEffectsItemComponent::class -> SuspiciousStewEffectsItemComponent(this.readAppliedPotionEffectsList())
        WritableBookContentItemComponent::class -> WritableBookContentItemComponent(this.readBookPages())
        WrittenBookContentItemComponent::class -> WrittenBookContentItemComponent(
            this.readString(),
            this.readOptionalOrNull<String>(),
            this.readString(),
            this.readVarInt(),
            this.readBookPages()
        )

        TrimItemComponent::class -> TrimItemComponent(
            TrimMaterialRegistry[this.readString()],
            TrimPatternRegistry[this.readString()],
            this.readBoolean()
        )

        DebugStickItemComponent::class -> DebugStickItemComponent(this.readNBT() as NBTCompound)
        EntityDataItemComponent::class -> EntityDataItemComponent(this.readNBT() as NBTCompound)
        BucketEntityDataItemComponent::class -> BucketEntityDataItemComponent(this.readNBT() as NBTCompound)
        BlockEntityDataItemComponent::class -> BlockEntityDataItemComponent(this.readNBT() as NBTCompound)
        NoteBlockInstrumentItemComponent::class -> NoteBlockInstrumentItemComponent(this.readString())
        OminousBottleAmplifierItemComponent::class -> OminousBottleAmplifierItemComponent(this.readVarInt())
        JukeboxPlayableItemComponent::class -> {
            val directMode = this.readBoolean()
            val identifier = if (directMode) this.readString() else null
            val type = if (directMode) this.readVarInt() else null
            val sound = if (directMode) this.readSoundEvent() else null
            val description = if (directMode) (this.readNBT() as NBTCompound).toComponent() else null
            val duration = if (directMode) this.readFloat() else null
            val output = if (directMode) this.readVarInt() else null

            val showInTooltip = this.readBoolean()
            JukeboxPlayableItemComponent(directMode, identifier, description, duration, output, showInTooltip)
        }

        RecipesItemComponent::class -> RecipesItemComponent(this.readStringList())
        LodestoneTrackerItemComponent::class -> {
            val hasGlobalPosition = this.readBoolean()
            val dimensionIdentifier = this.readString()
            val world = WorldManager.worlds[dimensionIdentifier]
                ?: throw Exception("there is no world with the identifier (name) $dimensionIdentifier")
            val position = this.readBlockPosition()
            val location = position.toLocation(world)
            val tracked = this.readBoolean()

            LodestoneTrackerItemComponent(hasGlobalPosition, world, location, tracked)
        }

        FireworkExplosionItemComponent::class -> FireworkExplosionItemComponent(
            this.readVarIntEnum<FireworkShape>(),
            this.readCustomColorList(),
            this.readCustomColorList(),
            this.readBoolean(),
            this.readBoolean()
        )

        FireworksItemComponent::class -> FireworksItemComponent(this.readByte(), this.readFireworkExplosionList())
        PlayerHeadProfileItemComponent::class -> PlayerHeadProfileItemComponent(
            this.readOptionalOrNull<String>(),
            this.readOptionalOrNull<UUID>(),
            this.readProfilePropertyMap()
        )

        NoteBlockSoundItemComponent::class -> NoteBlockSoundItemComponent(this.readString())
        BannerPatternsItemComponent::class -> BannerPatternsItemComponent(this.readBannerPatternLayerList())
        BaseColorItemComponent::class -> BaseColorItemComponent(this.readVarIntEnum<DyeColor>())
        PotDecorationsItemComponent::class -> PotDecorationsItemComponent()
        ContainerItemComponent::class -> ContainerItemComponent(this.readItemStackList())
        BlockStateItemComponent::class -> {
            val map = mutableMapOf<String, String>()
            for (i in 0 until this.readVarInt()) {
                map[this.readString()] = this.readString()
            }
            return BlockStateItemComponent(map)
        }

        BeesItemComponent::class -> {
            val bees = mutableListOf<BeeInsideBeehive>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                val data = this.readNBT() as NBTCompound
                val ticksInHive = this.readVarInt()
                val minTicksInHive = this.readVarInt()
                bees.add(BeeInsideBeehive(data, ticksInHive, minTicksInHive))
            }
            BeesItemComponent(bees)
        }

        LockItemComponent::class -> LockItemComponent(this.readNBT() as NBTString)
        ContainerLootItemComponent::class -> ContainerLootItemComponent(this.readNBT() as NBTCompound)
        else -> throw Exception("Tried to read item component with id $id but that id does not exist or is not implement yet!")
    }
}