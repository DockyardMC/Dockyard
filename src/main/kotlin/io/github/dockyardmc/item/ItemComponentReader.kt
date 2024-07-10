package io.github.dockyardmc.item

import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.sounds.readSoundEvent
import io.github.dockyardmc.world.WorldManager
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound

fun ByteBuf.readComponent(id: Int, buf: ByteBuf): ItemComponent {
    return when (id) {
        0 -> CustomDataItemComponent(buf.readNBT() as NBTCompound)
        1 -> MaxStackSizeItemComponent(buf.readVarInt())
        2 -> MaxDamageItemComponent(buf.readVarInt())
        3 -> DamageItemComponent(buf.readVarInt())
        4 -> UnbreakableItemComponent(buf.readBoolean())
        //TODO 5 -> return NBT to COMPONENT
        //TODO 6 -> return NBT to COMPONENT
        //TODO 7 -> return list of NBT to COMPONENT
        8 -> RarityItemComponent(buf.readVarIntEnum<ItemRarity>())
        //TODO 9 -> Enchantments
        //TODO 10 -> return CanBePlacedOnItemComponent()
        //TODO 11 -> return CanBePlacedOnItemComponent()
        //TODO 12 -> return CanBePlacedOnItemComponent()
        13 -> CustomModelDataItemComponent(buf.readVarInt())
        14 -> HideAdditionalTooltipItemComponent()
        15 -> HideTooltipItemComponent()
        16 -> RepairCostItemComponent(buf.readVarInt())
        17 -> CreativeSlotLockItemComponent()
        18 -> EnchantmentGlintOverrideItemComponent(buf.readVarInt().toBoolean())
        19 -> IntangibleProjectileItemComponent()
        20 -> FoodItemComponent(buf.readVarInt(), true, buf.readBoolean(), buf.readFloat()) //TODO Potion Effects
        21 -> FireResistantItemComponent()
        22 -> {
            val rules = mutableListOf<ToolRule>()
            val size = buf.readVarInt()
            for (i in 0 until size) {
                //TODO Block Set read
                val speed = if (buf.readBoolean()) buf.readFloat() else null
                val correctDropForBlocks = if (buf.readBoolean()) buf.readBoolean() else null
                rules.add(ToolRule(listOf(), speed, correctDropForBlocks))
            }
            val defaultMiningSpeed = buf.readFloat()
            val damagePerBlock = buf.readVarInt()
            ToolItemComponent(rules, defaultMiningSpeed, damagePerBlock)
        }
        //TODO 23 -> Enchantment
        24 -> DyedColorItemComponent(CustomColor.fromRGBInt(buf.readInt()), buf.readBoolean())
        25 -> MapColorItemComponent(CustomColor.fromRGBInt(buf.readInt()))
        26 -> MapIdItemComponent(buf.readVarInt())
        27 -> MapDecorationsItemComponent(buf.readNBT() as NBTCompound)
        28 -> MapPostProcessingItemComponent(buf.readVarIntEnum<MapPostProcessing>())
        29 -> {
            val projectiles = mutableListOf<ItemStack>()
            val size = buf.readVarInt()
            for (i in 0 until size) {
                projectiles.add(buf.readItemStack())
            }
            ChargedProjectilesItemComponent(projectiles)
        }

        30 -> {
            val bundleContents = mutableListOf<ItemStack>()
            val size = buf.readVarInt()
            for (i in 0 until size) {
                bundleContents.add(buf.readItemStack())
            }
            BundleContentsItemComponent(bundleContents)
        }
        //TODO 31 -> PotionsContent
        //TODO 32 -> Sus stew
        33 -> WritableBookContentItemComponent(buf.readBookPages())
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
        36 -> DebugStickItemComponent(buf.readNBT() as NBTCompound)
        37 -> EntityDataItemComponent(buf.readNBT() as NBTCompound)
        38 -> BucketEntityDataItemComponent(buf.readNBT() as NBTCompound)
        39 -> BlockEntityDataItemComponent(buf.readNBT() as NBTCompound)
        40 -> {
            val type = buf.readVarInt()
            NoteBlockInstrumentItemComponent(buf.readSoundEvent(), buf.readFloat(), buf.readFloat())
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

            val showInTooltip = buf.readBoolean()
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
        48 -> NoteBlockSoundItemComponent(buf.readUtf())
        //TODO 49 -> Banner Patterns
        50 -> BannerShieldBaseColorItemComponent(buf.readVarIntEnum<LegacyTextColor>())
        //TODO 51 -> Pot Decorations
        52 -> {
            val containerItems = mutableListOf<ItemStack>()
            val size = buf.readVarInt()
            for (i in 0 until size) {
                containerItems.add(buf.readItemStack())
            }
            ContainerItemComponent(containerItems)
        }
        //TODO 53 -> Block State Component
        54 -> {
            val bees = mutableListOf<BeeInsideBeehive>()
            val size = buf.readVarInt()
            for (i in 0 until size) {
                val data = buf.readNBT() as NBTCompound
                val ticksInHive = buf.readVarInt()
                val minTicksInHive = buf.readVarInt()
                bees.add(BeeInsideBeehive(data, ticksInHive, minTicksInHive))
            }
            BeesItemComponent(bees)
        }

        55 -> LockItemComponent(buf.readNBT() as NBTCompound)
        56 -> ContainerLootItemComponent(buf.readNBT() as NBTCompound)
        else -> throw Exception("Tried to read item component with id $id but that id does not exist or is not implement yet!")
    }
}