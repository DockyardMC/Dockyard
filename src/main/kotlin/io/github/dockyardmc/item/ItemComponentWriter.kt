package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.writeAttribute
import io.github.dockyardmc.blocks.BlockSet
import io.github.dockyardmc.blocks.writeBlockPredicate
import io.github.dockyardmc.blocks.writeBlockSet
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.writeBlockPosition
import io.github.dockyardmc.player.writeProfileProperties
import io.github.dockyardmc.sounds.writeSoundEvent
import io.netty.buffer.ByteBuf

fun ByteBuf.writeItemComponent(component: ItemComponent) {
    this.writeVarInt(ItemComponents.components.indexOf(component::class))

    when (component) {
        is CustomDataItemComponent -> this.writeNBT(component.data)
        is MaxStackSizeItemComponent -> this.writeVarInt(component.maxStackSize)
        is MaxDamageItemComponent -> this.writeVarInt(component.maxDamage)
        is DamageItemComponent -> this.writeVarInt(component.damage)
        is UnbreakableItemComponent -> this.writeBoolean(component.showInTooltip)
        is CustomNameItemComponent -> this.writeTextComponent(component.name)
        is ItemNameItemComponent -> this.writeTextComponent(component.name)
        is ItemModelItemComponent -> this.writeString(component.model)
        is LoreItemComponent -> {
            this.writeVarInt(component.lines.size)
            component.lines.forEach {
                this.writeTextComponent(it)
            }
        }

        is RarityItemComponent -> this.writeVarIntEnum(component.rarity)
        //TODO EnchantmentsItemComponent
        is CanBePlacedOnItemComponent -> {
            this.writeVarInt(component.blocks.size)
            component.blocks.forEach(this::writeBlockPredicate)
        }

        is CanBreakItemComponent -> {
            this.writeVarInt(component.blocks.size)
            component.blocks.forEach(this::writeBlockPredicate)
        }

        is AttributeModifiersItemComponent -> {
            this.writeVarInt(component.attributes.size)
            component.attributes.forEach { this.writeAttribute(it) }
            this.writeBoolean(component.showInTooltip)
        }

        is CustomModelDataItemComponent -> this.writeVarInt(component.customModelData)
        is HideAdditionalTooltipItemComponent -> {}
        is RepairCostItemComponent -> this.writeVarInt(component.repairCost)
        is CreativeSlotLockItemComponent -> {}
        is EnchantmentGlintOverrideItemComponent -> this.writeVarInt(component.hasGlint.toInt())
        is IntangibleProjectileItemComponent -> {}
        is FoodItemComponent -> {
            this.writeVarInt(component.nutrition)
            this.writeFloat(component.saturation)
            this.writeBoolean(component.canAlwaysEat)
        }

        is ConsumableItemComponent -> {
            this.writeFloat(component.consumeSeconds)
            this.writeVarIntEnum(component.animation)
            this.writeSoundEvent(component.sound.identifier)
            this.writeBoolean(component.hasConsumeParticles)
            this.writeConsumeEffects(component.consumeEffects)
        }

        is UseRemainderItemComponent -> this.writeItemStack(component.itemStack)
        is UseCooldownItemComponent -> {
            this.writeFloat(component.cooldownSeconds)
            this.writeOptional(component.cooldownGroup) { it.writeString(component.cooldownGroup!!) }
        }

        is DamageResistantItemComponent -> this.writeString(component.type.identifier)
        is ToolItemComponent -> {
            this.writeVarInt(component.toolRules.size)
            component.toolRules.forEach { rule ->
                this.writeBlockSet(
                    BlockSet(
                        0,
                        tagName = null,
                        blockIds = rule.blocks.map { block -> block.getProtocolId() })
                )
                this.writeOptional(rule.speed) { it.writeFloat(rule.speed!!) }
                this.writeOptional(rule.correctDropForBlocks) { it.writeBoolean(rule.correctDropForBlocks!!) }
            }
            this.writeFloat(component.defaultMiningSpeed)
            this.writeVarInt(component.damagePerBlock)
        }

        is EnchantableItemComponent -> this.writeVarInt(component.value)
        is EquippableItemComponent -> {
            this.writeVarIntEnum<EquipmentSlot>(component.slot)
            this.writeSoundEvent(component.equipSound.identifier)
            this.writeOptional(component.model) { it.writeString(component.model!!) }
            this.writeOptional(component.cameraOverlay) { it.writeString(component.cameraOverlay!!) }

            this.writeVarInt(component.allowedEntities.size)
            component.allowedEntities.forEach { this.writeString(it.identifier) }

            this.writeBoolean(component.dispensable)
            this.writeBoolean(component.swappable)
            this.writeBoolean(component.damageOnHurt)
        }

        is RepairableItemComponent -> {
            this.writeVarInt(component.materials.size)
            component.materials.forEach { this.writeString(it.identifier) }
        }

        is GliderItemComponent -> {}
        is TooltipStyleItemComponent -> this.writeString(component.texture)
        is DeathProtectionItemComponent -> this.writeConsumeEffects(component.effects)
        is StoredEnchantments -> {
            this.writeStringArray(component.enchantments)
            this.writeBoolean(component.showInTooltip)
        }

        is DyedColorItemComponent -> {
            this.writeInt(component.color.toRgbInt())
            this.writeBoolean(component.showInTooltip)
        }

        is MapColorItemComponent -> {
            this.writeInt(component.color.toRgbInt())
        }

        is MapIdItemComponent -> this.writeVarInt(component.mapId)
        is MapDecorationsItemComponent -> this.writeNBT(component.nbt)
        is MapPostProcessingItemComponent -> this.writeVarIntEnum<MapPostProcessing>(component.type)
        is ChargedProjectilesItemComponent -> this.writeItemStackList(component.projectiles)
        is BundleContentsItemComponent -> this.writeItemStackList(component.items)
        is PotionContentsItemComponent -> {
            this.writeOptional(component.potion) { this.writeVarInt(component.potion!!.getProtocolId()) }
            this.writeOptional(component.customColor) { this.writeVarInt(component.customColor!!.toRgbInt()) }
            this.writeAppliedPotionEffectsList(component.potionEffects)
            this.writeOptional(component.customName) { this.writeString(component.customName!!) }
        }

        is SuspiciousStewEffectsItemComponent -> this.writeAppliedPotionEffectsList(component.potionEffects)
        is WritableBookContentItemComponent -> this.writeBookPages(component.pages)
        is WrittenBookContentItemComponent -> {
            this.writeString(component.title)
            this.writeOptional(component.filteredTitle) { this.writeString(component.filteredTitle!!) }
            this.writeString(component.author)
            this.writeVarInt(component.generation)
            this.writeBookPages(component.pages)
        }

        is TrimItemComponent -> {
            this.writeString(component.material.identifier)
            this.writeString(component.pattern.identifier)
            this.writeBoolean(component.showInTooltip)
        }

        is DebugStickItemComponent -> this.writeNBT(component.data)
        is EntityDataItemComponent -> this.writeNBT(component.data)
        is BucketEntityDataItemComponent -> this.writeNBT(component.data)
        is BlockEntityDataItemComponent -> this.writeNBT(component.data)
        is NoteBlockInstrumentItemComponent -> this.writeString(component.instrument)
        is OminousBottleAmplifierItemComponent -> this.writeVarInt(component.amplifier)
        //TODO who tf uses this anyway Im not dealing with it     is JukeboxPlayableItemComponent -> {}
        is RecipesItemComponent -> {
            this.writeStringArray(component.recipes)
        }

        is LodestoneTrackerItemComponent -> {
            this.writeBoolean(component.hasGlobalPosition)
            this.writeString(component.dimension.name)
            this.writeBlockPosition(component.position)
            this.writeBoolean(component.tracked)
        }

        is FireworkExplosionItemComponent -> {
            this.writeFireworkExplosion(component)
        }

        is FireworksItemComponent -> {
            this.writeByte(component.flightDuration.toInt())
            this.writeVarInt(component.explosions.size)
            component.explosions.forEach { this.writeFireworkExplosion(it) }
        }

        is PlayerHeadProfileItemComponent -> {
            this.writeOptional(component.name) { this.writeString(component.name!!) }
            this.writeOptional(component.uuid) { this.writeUUID(component.uuid!!) }
            this.writeProfileProperties(component.propertyMap)
        }

        is NoteBlockSoundItemComponent -> this.writeString(component.sound)
        is BannerPatternsItemComponent -> this.writeBannerPatternLayerList(component.layers)
        is BaseColorItemComponent -> this.writeVarIntEnum(component.color)
        is PotDecorationsItemComponent -> {}
        is ContainerItemComponent -> this.writeItemStackList(component.items)
        is BlockStateItemComponent -> {
            this.writeVarInt(component.states.size)
            component.states.forEach {
                this.writeString(it.key)
                this.writeString(it.value)
            }
        }

        is BeesItemComponent -> {
            this.writeVarInt(component.bees.size)
            component.bees.forEach {
                this.writeNBT(it.entityData)
                this.writeVarInt(it.ticksInHive)
                this.writeVarInt(it.minTicksInHive)
            }
        }

        is LockItemComponent -> this.writeNBT(component.key)
        is ContainerLootItemComponent -> this.writeNBT(component.loot)
        else -> throw Exception("Tried to write ${component::class.simpleName} but that writer for that component is not implemented!")
    }
}