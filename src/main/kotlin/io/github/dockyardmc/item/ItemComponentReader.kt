package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.Attribute
import io.github.dockyardmc.attributes.readAttribute
import io.github.dockyardmc.blocks.BlockPredicate
import io.github.dockyardmc.blocks.readBlockPredicate
import io.github.dockyardmc.blocks.readBlockSet
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.readBlockPosition
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.registries.PotionEffect
import io.github.dockyardmc.registry.registries.PotionEffectRegistry
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.readSoundEvent
import io.github.dockyardmc.world.WorldManager
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

fun ByteBuf.readComponent(id: Int): ItemComponent {
    return when (id) {
        0 -> CustomDataItemComponent(this.readNBT() as NBTCompound)
        1 -> MaxStackSizeItemComponent(this.readVarInt())
        2 -> MaxDamageItemComponent(this.readVarInt())
        3 -> DamageItemComponent(this.readVarInt())
        4 -> UnbreakableItemComponent(this.readBoolean())
        5 -> {
            val nbt = this.readNBT() as NBTCompound
            return CustomNameItemComponent(nbt.toComponent())
        }

        6 -> {
            val nbt = this.readNBT() as NBTCompound
            return ItemNameItemComponent(nbt.toComponent())
        }

        7 -> {
            val size = this.readVarInt()
            val lines = mutableListOf<Component>()
            for (i in 0 until size) {
                val nbt = this.readNBT() as NBTCompound
                lines.add(nbt.toComponent())
            }
            return LoreItemComponent(lines)
        }

        8 -> RarityItemComponent(this.readVarIntEnum<ItemRarity>())
        //TODO 9 -> Enchantments
        9 -> {
            val size = this.readVarInt()
            for (i in 0 until size) {
                this.readVarInt()
                this.readVarInt()
            }
            this.readBoolean()
            return EnchantmentsItemComponent()
        }

        10 -> {
            val size = this.readVarInt()
            val predicates = mutableListOf<BlockPredicate>()
            for (i in 0 until size) {
                predicates.add(this.readBlockPredicate())
            }
            val showInTooltip = this.readBoolean()

            return CanBePlacedOnItemComponent(predicates, showInTooltip)
        }

        11 -> {
            val size = this.readVarInt()
            val predicates = mutableListOf<BlockPredicate>()
            for (i in 0 until size) {
                predicates.add(this.readBlockPredicate())
            }
            val showInTooltip = this.readBoolean()

            return CanBreakItemComponent(predicates, showInTooltip)
        }

        12 -> {
            val size = this.readVarInt()
            val attributes = mutableListOf<Attribute>()
            for (i in 0 until size) attributes.add(this.readAttribute())

            val showInTooltip = this.readBoolean()
            return AttributeModifiersItemComponent(attributes, showInTooltip)
        }

        13 -> CustomModelDataItemComponent(this.readVarInt())
        14 -> HideAdditionalTooltipItemComponent()
        15 -> HideTooltipItemComponent()
        16 -> RepairCostItemComponent(this.readVarInt())
        17 -> CreativeSlotLockItemComponent()
        18 -> EnchantmentGlintOverrideItemComponent(this.readVarInt() == 1)
        19 -> IntangibleProjectileItemComponent()
        20 -> {

            val food = this.readVarInt()
            val saturation = this.readFloat() // memory leak was here!!

            return FoodItemComponent(food, true, this.readBoolean(), this.readFloat())
        }

        21 -> FireResistantItemComponent()
        22 -> {
            val rules = mutableListOf<ToolRule>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                val blocks = this.readBlockSet()
                val speed = if (this.readBoolean()) this.readFloat() else null
                val correctDropForBlocks = if (this.readBoolean()) this.readBoolean() else null
                rules.add(ToolRule(listOf(), speed, correctDropForBlocks))
            }
            val defaultMiningSpeed = this.readFloat()
            val damagePerBlock = this.readVarInt()
            ToolItemComponent(rules, defaultMiningSpeed, damagePerBlock)
        }
        //TODO 23 -> Stored Enchantments
        23 -> {
            val size = this.readVarInt()
            for (i in 0 until size) {
                this.readVarInt()
                this.readVarInt()
            }
            this.readBoolean()
            return StoredEnchantmentsItemComponent()
        }

        24 -> DyedColorItemComponent(CustomColor.fromRGBInt(this.readInt()), this.readBoolean())
        25 -> MapColorItemComponent(CustomColor.fromRGBInt(this.readInt()))
        26 -> MapIdItemComponent(this.readVarInt())
        27 -> MapDecorationsItemComponent(this.readNBT() as NBTCompound)
        28 -> MapPostProcessingItemComponent(this.readVarIntEnum<MapPostProcessing>())
        29 -> {
            val projectiles = mutableListOf<ItemStack>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                projectiles.add(this.readItemStack())
            }
            ChargedProjectilesItemComponent(projectiles)
        }

        30 -> {
            val bundleContents = mutableListOf<ItemStack>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                bundleContents.add(this.readItemStack())
            }
            BundleContentsItemComponent(bundleContents)
        }

        31 -> {
            val potionId = if (this.readBoolean()) this.readVarInt() else null
            val customColor = if (this.readBoolean()) CustomColor.fromRGBInt(this.readVarInt()) else null
            val effects = mutableListOf<AppliedPotionEffect>()
            for (i in 0 until this.readVarInt()) effects.add(this.readAppliedPotionEffect())

            return PotionContentsItemComponent(potionId, customColor, effects)
        }

        32 -> {
            val effects = mutableListOf<PotionEffect>()
            for (i in 0 until this.readVarInt()) {
                PotionEffectRegistry.getByProtocolId(this.readVarInt())
            }

            return SuspiciousStewEffectsItemComponent(effects)
        }

        33 -> WritableBookContentItemComponent(this.readBookPages())
        34 -> {
            val title = this.readString()
            val filteredTitle = if (this.readBoolean()) this.readString() else null
            val author = this.readString()
            val generation = this.readVarInt()
            val pages = this.readBookPages()
            this.readBoolean()
            WrittenBookContentItemComponent(title, filteredTitle, author, generation, pages)
        }

        35 -> TODO("Trims are not implemented")
        36 -> DebugStickItemComponent(this.readNBT() as NBTCompound)
        37 -> EntityDataItemComponent(this.readNBT() as NBTCompound)
        38 -> BucketEntityDataItemComponent(this.readNBT() as NBTCompound)
        39 -> BlockEntityDataItemComponent(this.readNBT() as NBTCompound)
        40 -> {
            val type = this.readVarInt()
            NoteBlockInstrumentItemComponent(this.readSoundEvent(), this.readFloat(), this.readFloat())
        }

        41 -> OminousBottleAmplifierItemComponent(this.readVarInt())
        42 -> {
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

        43 -> RecipesItemComponent(this.readNBT() as NBTCompound)
        44 -> {
            val hasGlobalPosition = this.readBoolean()
            val dimensionIdentifier = this.readString()
            val world = WorldManager.worlds[dimensionIdentifier]
                ?: throw Exception("there is no world with the identifier (name) $dimensionIdentifier")
            val position = this.readBlockPosition()
            val location = position.toLocation(world)
            val tracked = this.readBoolean()

            LodestoneTrackerItemComponent(hasGlobalPosition, world, location, tracked)
        }

        45 -> TODO("Firework Explosion Data is not implemented yet")
        46 -> TODO("Fireworks are not implemented yet")
        47 -> TODO("Player head profiles are not implemented yet")
        48 -> NoteBlockSoundItemComponent(this.readString())
        49 -> TODO("Banner Patterns are not implemented yet")
        50 -> BannerShieldBaseColorItemComponent(this.readVarIntEnum<LegacyTextColor>())
        51 -> TODO("Pot Decorations are not implemented yet")
        52 -> {
            val containerItems = mutableListOf<ItemStack>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                containerItems.add(this.readItemStack())
            }
            ContainerItemComponent(containerItems)
        }

        53 -> {
            val states = mutableMapOf<String, String>()
            for (i in 0 until this.readVarInt()) {
                states[this.readString()] = this.readString()
            }

            BlockStateItemComponent(states)
        }

        54 -> {
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

        55 -> LockItemComponent(this.readNBT() as NBTCompound)
        56 -> ContainerLootItemComponent(this.readNBT() as NBTCompound)
        else -> throw Exception("Tried to read item component with id $id but that id does not exist or is not implement yet!")
    }
}