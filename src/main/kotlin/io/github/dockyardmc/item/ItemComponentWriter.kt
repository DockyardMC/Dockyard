package io.github.dockyardmc.item

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.writeBlockPosition
import io.github.dockyardmc.player.writeProfileProperties
import io.github.dockyardmc.registry.Items
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.scroll.extensions.toComponent
import io.github.dockyardmc.sounds.writeSoundEvent
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBT

fun ByteBuf.writeItemComponent(comp: ItemComponent) {
    this.writeVarInt(comp.id)
    when(comp) {
        is CustomDataItemComponent -> {
            this.writeNBT(comp.data)
        }
        is MaxStackSizeItemComponent -> {
            this.writeVarInt(comp.maxStackSize)
        }
        is MaxDamageItemComponent -> {
            this.writeVarInt(comp.maxDamage)
        }
        is DamageItemComponent -> {
            this.writeVarInt(comp.damage)
        }
        is UnbreakableItemComponent -> {
            this.writeBoolean(comp.showInTooltip)
        }
        is CustomNameItemComponent -> {
            this.writeTextComponent(comp.name)
        }
        is ItemNameItemComponent -> {
            this.writeNBT(comp.name.toNBT())
        }
        is LoreItemComponent -> {
            this.writeVarInt(comp.lines.size)
            comp.lines.forEach { this.writeTextComponent(it) }
        }
        is RarityItemComponent -> {
            this.writeVarIntEnum<ItemRarity>(comp.rarity)
        }
        //TODO Enchantments

        //TODO figure out Block Predicates
        is CanBePlacedOnItemComponent -> {
        }
        //TODO figure out Block Predicates
        is CanBreakItemComponent -> {
        }
        //TODO figure out item attributes
        is AttributeModifiersItemComponent -> {
        }
        is CustomModelDataItemComponent -> {
            this.writeVarInt(comp.customModelData)
        }
        is HideAdditionalTooltipItemComponent -> {} // Empty
        is HideTooltipItemComponent -> {} // Empty
        is RepairCostItemComponent -> {
            this.writeVarInt(comp.repairCost)
        }
        is CreativeSlotLockItemComponent -> {} // Empty
        is EnchantmentGlintOverrideItemComponent -> {
            this.writeBoolean(comp.hasGlint)
        }

        //The Notchian client utilizes the codec meant for
        //chat parsing (and NBT in general) when handling this component, even
        //though it contains no data. This causes an empty
        //Compound Tag to be written, which is likely an unintended bug.
        is IntangibleProjectileItemComponent -> {
            val emptyCompound = NBT.Compound()
            this.writeNBT(emptyCompound)
        }
        is FoodItemComponent -> {
            this.writeVarInt(comp.nutrition)
//            this.writeBoolean(comp.saturation)
            this.writeFloat(0f)
            this.writeBoolean(comp.canAlwaysEat)
            this.writeFloat(comp.secondsToEat)
            this.writeItemStack(ItemStack(Items.AIR, 1))
            //TODO Potion Effects
            this.writeVarInt(0) //temp: 0 effects
        }

        is DamageResistantItemComponent -> {} // Empty
        is ToolItemComponent -> {
            this.writeVarInt(comp.toolRules.size)
            comp.toolRules.forEach {
                //TODO Block Set
                this.writeBoolean(it.speed != null)
                if(it.speed != null) this.writeFloat(it.speed)
                this.writeBoolean(it.correctDropForBlocks != null)
                if(it.correctDropForBlocks != null) this.writeBoolean(it.correctDropForBlocks)
            }
            this.writeFloat(comp.defaultMiningSpeed)
            this.writeVarInt(comp.damagePerBlock)
        }
        //TODO StoredEnchantments
        is DyedColorItemComponent -> {
            this.writeInt(comp.color.toRgbInt())
            this.writeBoolean(comp.showInTooltip)
        }
        is MapColorItemComponent -> {
            this.writeInt(comp.color.toRgbInt())
        }
        is MapIdItemComponent -> {
            this.writeVarInt(comp.mapId)
        }
        is MapDecorationsItemComponent -> {
            this.writeNBT(comp.nbt)
        }
        is MapPostProcessingItemComponent -> {
            this.writeVarIntEnum<MapPostProcessing>(comp.type)
        }
        is ChargedProjectilesItemComponent -> {
            this.writeVarInt(comp.projectiles.size)
            comp.projectiles.forEach(this::writeItemStack)
        }
        is BundleContentsItemComponent -> {
            this.writeVarInt(comp.items.size)
            comp.items.forEach(this::writeItemStack)
        }
        //TODO PotionsContent
        //TODO Sus Stew
        is WritableBookContentItemComponent -> {
            this.writeBookPages(comp.pages)
        }
        is WrittenBookContentItemComponent -> {
            this.writeString(comp.title)
            this.writeOptional(comp.filteredTitle) { op -> op.writeString(comp.filteredTitle!!) }
            this.writeString(comp.author)
            this.writeVarInt(comp.generation)
            this.writeBookPages(comp.pages)
            this.writeBoolean(false)
        }
        //TODO Trims
        is DebugStickItemComponent -> {
            this.writeNBT(comp.data)
        }
        is EntityDataItemComponent -> {
            this.writeNBT(comp.data)
        }
        is BucketEntityDataItemComponent -> {
            this.writeNBT(comp.data)
        }
        is BlockEntityDataItemComponent -> {
            this.writeNBT(comp.data)
        }
        is NoteBlockInstrumentItemComponent -> {
            this.writeVarInt(0)
            this.writeSoundEvent(comp.instrument)
            this.writeFloat(comp.maxSoundRange)
            this.writeFloat(comp.currentRange)
        }
        is OminousBottleAmplifierItemComponent -> {
            this.writeVarInt(comp.amplifier)
        }
        is JukeboxPlayableItemComponent -> {
            this.writeBoolean(comp.directMode)
            if(!comp.directMode) {
                this.writeString(comp.sound!!)
            } else {
                this.writeVarInt(0)
                this.writeSoundEvent(comp.sound!!)
                this.writeTextComponent(comp.description!!)
                this.writeFloat(comp.duration!!)
                this.writeVarInt(comp.output!!)
            }
            this.writeBoolean(comp.showInTooltip)
        }
        is RecipesItemComponent -> {
            this.writeNBT(comp.data)
        }
        is LodestoneTrackerItemComponent -> {
            this.writeBoolean(comp.hasGlobalPosition)
            this.writeString(comp.dimension.name)
            this.writeBlockPosition(comp.position)
            this.writeBoolean(comp.tracked)
        }
        //TODO Firework Explosion
        //TODO Fireworks
        is PlayerHeadProfileItemComponent -> {
            this.writeOptional(comp.name) { it.writeString(comp.name!!) }
            this.writeOptional(comp.uuid) { it.writeUUID(comp.uuid!!) }
            this.writeProfileProperties(comp.propertyMap, disableUtf = true)
        }

        is NoteBlockSoundItemComponent -> {
            this.writeString(comp.sound)
        }
        //TODO Banner Patterns
        is BannerShieldBaseColorItemComponent -> {
            this.writeVarIntEnum<LegacyTextColor>(comp.color)
        }
        //TODO Pot Decorations
        is ContainerItemComponent -> {
            this.writeItemStackList(comp.items)
        }
        //TODO Block State
        is BeesItemComponent -> {
            this.writeVarInt(comp.bees.size)
            comp.bees.forEach {
                this.writeNBT(it.entityData)
                this.writeVarInt(it.ticksInHive)
                this.writeVarInt(it.minTicksInHive)
            }
        }
        is LockItemComponent -> {
            this.writeNBT(comp.key)
        }
        is ContainerLootItemComponent -> {
            this.writeNBT(comp.loot)
        }

        else -> throw Exception("${comp::class.simpleName} is missing implementation in ItemComponentWriter")
    }
}

fun Collection<String>.toComponents(): List<Component> {
    val list = mutableListOf<Component>()
    this.forEach { list.add(it.toComponent()) }
    return list
}