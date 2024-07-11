package io.github.dockyardmc.item

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor
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
        //TODO 5 -> return NBT to COMPONENT
        5 -> {
            this.readNBT()
            return CustomNameItemComponent("TODO")
        }
        //TODO 6 -> return NBT to COMPONENT
        6 -> {
            this.readNBT()
            return ItemNameItemComponent("TODO")
        }
        //TODO 7 -> return list of NBT to COMPONENT
        7 -> {
            val size = this.readVarInt()
            for (i in 0 until size) {
                this.readNBT()
            }
            return LoreItemComponent(mutableListOf())
        }
        8 -> RarityItemComponent(this.readVarIntEnum<ItemRarity>())
        //TODO 9 -> Enchantments
        9 -> {
            val size = this.readVarInt()
            this.readBoolean()
            return EnchantmentsItemComponent()
        }
        //TODO 10 -> return CanBePlacedOnItemComponent()
        //TODO 11 -> return CanBePlacedOnItemComponent()
        //TODO 12 -> return Attreibute Modifiyingers
        12 -> {
            val size = this.readVarInt()
            val showInTooltip = this.readBoolean()
            return AttributeModifiersItemComponent()
        }
        13 -> CustomModelDataItemComponent(this.readVarInt())
        14 -> HideAdditionalTooltipItemComponent()
        15 -> HideTooltipItemComponent()
        16 -> RepairCostItemComponent(this.readVarInt())
        17 -> CreativeSlotLockItemComponent()
        18 -> EnchantmentGlintOverrideItemComponent(this.readVarInt().toBoolean())
        19 -> IntangibleProjectileItemComponent()
        20 -> {

            val food = this.readVarInt()
            val saturation = this.readFloat() // memory leak was here!!

            return FoodItemComponent(food, true, this.readBoolean(), this.readFloat())
        } //TODO Potion Effects
        21 -> FireResistantItemComponent()
        22 -> {
            val rules = mutableListOf<ToolRule>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                //TODO Block Set read
                val speed = if (this.readBoolean()) this.readFloat() else null
                val correctDropForBlocks = if (this.readBoolean()) this.readBoolean() else null
                rules.add(ToolRule(listOf(), speed, correctDropForBlocks))
            }
            val defaultMiningSpeed = this.readFloat()
            val damagePerBlock = this.readVarInt()
            ToolItemComponent(rules, defaultMiningSpeed, damagePerBlock)
        }
        //TODO 23 -> Enchantment
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
        //TODO 31 -> PotionsContent
        //TODO 32 -> Sus stew
        33 -> WritableBookContentItemComponent(this.readBookPages())
        34 -> {
            val title = this.readUtf()
            val filteredTitle = if (this.readBoolean()) this.readUtf() else null
            val author = this.readUtf()
            val generation = this.readVarInt()
            val pages = this.readBookPages()
            this.readBoolean()
            WrittenBookContentItemComponent(title, filteredTitle, author, generation, pages)
        }
        //TODO 35 -> trims
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
            val identifier = if (directMode) this.readUtf() else null
            val type = if (directMode) this.readVarInt() else null
            val sound = if (directMode) this.readSoundEvent() else null
            //TODO Text Component
            val duration = if (directMode) this.readFloat() else null
            val output = if (directMode) this.readVarInt() else null

            val showInTooltip = this.readBoolean()
            JukeboxPlayableItemComponent(directMode, identifier, "", duration, output, showInTooltip)
        }

        43 -> RecipesItemComponent(this.readNBT() as NBTCompound)
        44 -> {
            val hasGlobalPosition = this.readBoolean()
            val dimensionIdentifier = this.readUtf()
            val world = WorldManager.worlds[dimensionIdentifier]
                ?: throw Exception("there is no world with the identifier (name) $dimensionIdentifier")
            //TODO Actual position
            val location = Location(0, 0, 0, world)
            val tracked = this.readBoolean()

            LodestoneTrackerItemComponent(hasGlobalPosition, world, location, tracked)
        }
        //TODO 45 -> Firework Explosion Data
        //TODO 46 -> Firework Data
        //TODO 47 -> PlayerHeadProfileItemComponent()
        48 -> NoteBlockSoundItemComponent(this.readUtf())
        //TODO 49 -> Banner Patterns
        50 -> BannerShieldBaseColorItemComponent(this.readVarIntEnum<LegacyTextColor>())
        //TODO 51 -> Pot Decorations
        52 -> {
            val containerItems = mutableListOf<ItemStack>()
            val size = this.readVarInt()
            for (i in 0 until size) {
                containerItems.add(this.readItemStack())
            }
            ContainerItemComponent(containerItems)
        }
        //TODO 53 -> Block State Component
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