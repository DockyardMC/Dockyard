package io.github.dockyardmc.item

import cz.lukynka.BindableList
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.registry.BannerPattern
import io.github.dockyardmc.registry.Block
import io.github.dockyardmc.registry.Effects
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.scroll.LegacyTextColor
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import java.util.UUID
import kotlin.reflect.KClass

interface ItemComponent {
    val id: Int
}

data class CustomDataItemComponent(
    var data: NBTCompound,
    override val id: Int = 0
): ItemComponent

data class MaxStackSizeItemComponent(
    var maxStackSize: Int,
    override val id: Int = 1
): ItemComponent

data class MaxDamageItemComponent(
    var maxDamage: Int,
    override val id: Int = 2
): ItemComponent

data class DamageItemComponent(
    var damage: Int,
    override val id: Int = 3
): ItemComponent

data class UnbreakableItemComponent(
    var showInTooltip: Boolean = false,
    override val id: Int = 4
): ItemComponent

data class CustomNameItemComponent(
    var name: String,
    override val id: Int = 5
): ItemComponent

data class ItemNameItemComponent(
    var name: String,
    override val id: Int = 6
): ItemComponent

data class LoreItemComponent(
    var lines: Collection<String>,
    override val id: Int = 7
): ItemComponent

data class RarityItemComponent(
    var rarity: ItemRarity,
    override val id: Int = 8
): ItemComponent

//TODO Enchantments
data class EnchantmentsItemComponent(
    override val id: Int = 9
): ItemComponent

data class CanBePlacedOnItemComponent(
    var blocks: Collection<Block>,
    var showInTooltip: Boolean = true,
    override val id: Int = 10
): ItemComponent

data class CanBreakItemComponent(
    var blocks: Collection<Block>,
    var showInTooltip: Boolean = true,
    override val id: Int = 11
): ItemComponent

//TODO Attributes
data class AttributeModifiersItemComponent(
    override val id: Int = 12
): ItemComponent

data class CustomModelDataItemComponent(
    var customModelData: Int,
    override val id: Int = 13
): ItemComponent


//what is this? even wiki.vg doesnt know
data class HideAdditionalTooltipItemComponent(
    override val id: Int = 14
): ItemComponent

data class HideTooltipItemComponent(
    override val id: Int = 15
): ItemComponent

data class RepairCostItemComponent(
    var repairCost: Int,
    override val id: Int = 16
): ItemComponent

data class CreativeSlotLockItemComponent(
    override val id: Int = 17
): ItemComponent

data class EnchantmentGlintOverrideItemComponent(
    var hasGlint: Boolean,
    override val id: Int = 18
): ItemComponent


//note: write empty nbt compound as data
data class IntangibleProjectileItemComponent(
    override val id: Int = 19
): ItemComponent

//TODO Potion effects
data class FoodItemComponent(
    var nutrition: Int = 0,
    var giveSaturation: Boolean = false,
    var canAlwaysEat: Boolean = true,
    var secondsToEat: Float = 2f,
    var potionEffects: MutableList<Effects> = mutableListOf(),
    override val id: Int = 20
): ItemComponent

data class FireResistantItemComponent(
    override val id: Int = 21
): ItemComponent

data class ToolItemComponent(
    var toolRules: Collection<ToolRule>,
    var defaultMiningSpeed: Float,
    var damagePerBlock: Int,
    override val id: Int = 22
): ItemComponent

//TODO Enchantments
data class StoredEnchantmentsItemComponent(
    override val id: Int = 23
): ItemComponent

data class DyedColorItemComponent(
    var color: CustomColor,
    var showInTooltip: Boolean = false,
    override val id: Int = 24
): ItemComponent

data class MapColorItemComponent(
    var color: CustomColor,
    override val id: Int = 25
): ItemComponent

data class MapIdItemComponent(
    var mapId: Int,
    override val id: Int = 26
): ItemComponent

data class MapDecorationsItemComponent(
    var nbt: NBTCompound,
    override val id: Int = 27
): ItemComponent

data class MapPostProcessingItemComponent(
    var type: MapPostProcessing,
    override val id: Int = 28
): ItemComponent

data class ChargedProjectilesItemComponent(
    var projectiles: Collection<ItemStack>,
    override val id: Int = 29
): ItemComponent

data class BundleContentsItemComponent(
    var items: Collection<ItemStack>,
    override val id: Int = 30
): ItemComponent


//TODO Potions
data class PotionContentsItemComponent(
    var potionId: Int,
    var customColor: CustomColor?,
//    var potionEffects:

    override val id: Int = 31
): ItemComponent

//TODO Potions
data class SuspiciousStewEffectsItemComponent(
//    var potionEffects:
    override val id: Int = 32
): ItemComponent

data class WritableBookContentItemComponent(
    var pages: Collection<BookPage>,
    override val id: Int = 33
): ItemComponent

data class WrittenBookContentItemComponent(
    var title: String,
    var filteredTitle: String?,
    var author: String,
    var generation: Int,
    var pages: Collection<BookPage>,
    override val id: Int = 34
): ItemComponent

//TODO Armor trims
data class TrimItemComponent(
    override val id: Int = 35
): ItemComponent


data class DebugStickItemComponent(
    var data: NBTCompound,
    override val id: Int = 36
): ItemComponent

data class EntityDataItemComponent(
    var data: NBTCompound,
    override val id: Int = 37
): ItemComponent

data class BucketEntityDataItemComponent(
    var data: NBTCompound,
    override val id: Int = 38
): ItemComponent

data class BlockEntityDataItemComponent(
    var data: NBTCompound,
    override val id: Int = 39
): ItemComponent

data class NoteBlockInstrumentItemComponent(
    var instrument: String,
    var maxSoundRange: Float,
    var currentRange: Float,
    override val id: Int = 40
): ItemComponent

data class OminousBottleAmplifierItemComponent(
    var amplifier: Int,
    override val id: Int = 41
): ItemComponent

data class JukeboxPlayableItemComponent(
    var directMode: Boolean,
    var sound: String?,
    var description: String?,
    var duration: Float?,
    var output: Int? = 15,
    var showInTooltip: Boolean,
    override val id: Int = 42
): ItemComponent

data class RecipesItemComponent(
    var data: NBTCompound,
    override val id: Int = 43
): ItemComponent

data class LodestoneTrackerItemComponent(
    var hasGlobalPosition: Boolean,
    var dimension: World,
    var position: Location,
    var tracked: Boolean,
    override val id: Int = 44
): ItemComponent

//TODO FireworkExplosion data
data class FireworkExplosionItemComponent(
    override val id: Int = 45
): ItemComponent

//TODO Firework data
data class FireworksItemComponent(
    override val id: Int = 46
): ItemComponent

data class PlayerHeadProfileItemComponent(
    var name: String?,
    var uuid: UUID?,
    var propertyMap: ProfilePropertyMap,
    override val id: Int = 47
): ItemComponent

data class NoteBlockSoundItemComponent(
    var sound: String,
    override val id: Int = 48
): ItemComponent

data class BannerPatternsItemComponent(
    var layers: Collection<BannerPatternLayer>,
    override val id: Int = 49
): ItemComponent

data class BannerShieldBaseColorItemComponent(
    var color: LegacyTextColor,
    override val id: Int = 50
): ItemComponent

//TODO pot decoration registry
data class PotDecorationsItemComponent(
//    var decorations: Collection<>,
    override val id: Int = 51
): ItemComponent

data class ContainerItemComponent(
    var items: Collection<ItemStack>,
    override val id: Int = 52
): ItemComponent

//TODO Block States
data class BlockStateItemComponent(
    override val id: Int = 53
): ItemComponent

data class BeesItemComponent(
    var bees: Collection<BeeInsideBeehive>,
    override val id: Int = 54
): ItemComponent

data class LockItemComponent(
    var key: NBTCompound,
    override val id: Int = 55
): ItemComponent

data class ContainerLootItemComponent(
    var loot: NBTCompound,
    override val id: Int = 56
): ItemComponent


data class BeeInsideBeehive(
    var entityData: NBTCompound,
    var ticksInHive: Int,
    var minTicksInHive: Int
)

data class BannerPatternLayer(
    var pattern: BannerPattern,
    var translationKey: String,
    var color: CustomColor
)


data class BookPage(
    var rawContent: String,
    var filteredContent: String? = null
)

fun ByteBuf.writeBookPages(pages: Collection<BookPage>) {
    this.writeVarInt(pages.size)
    pages.forEach {
        this.writeUtf(it.rawContent)
        this.writeOptional(it.filteredContent) { op ->
            op.writeUtf(it.filteredContent!!)
        }
    }
}

fun ByteBuf.readBookPages(): List<BookPage> {
    val pages = mutableListOf<BookPage>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        val content = this.readUtf()
        val filteredContent = if(this.readBoolean()) this.readUtf() else null
        pages.add(BookPage(content, filteredContent))
    }
    return pages
}

fun BindableList<ItemComponent>.addOrUpdate(newComponent: ItemComponent) {
    if(this.values.firstOrNull { it::class == newComponent::class } != null) {
        val index = this.values.indexOfFirst { it::class == newComponent::class }
        this.setIndex(index, newComponent)
    } else {
        this.add(newComponent)
    }
}

fun BindableList<ItemComponent>.removeByType(type: KClass<*>) {
    this.values.forEach { if (it::class == type) this.remove(it) }
}

fun BindableList<ItemComponent>.hasType(type: KClass<*>): Boolean =
    this.values.firstOrNull { it::class == type } != null

@Suppress("UNCHECKED_CAST")
fun <T> BindableList<ItemComponent>.firstOrNullByType(type: KClass<*>): T? {
    val value = this.values.firstOrNull { it::class == type } ?: return null
    return value as T
}


data class ToolRule(
    val blocks: Collection<Block>,
    val speed: Float?,
    val correctDropForBlocks: Boolean?
)

enum class MapPostProcessing {
    LOCK,
    SCALE
}

enum class ItemRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC
}