package io.github.dockyardmc.item

import io.github.dockyardmc.attributes.Modifier
import io.github.dockyardmc.world.block.BlockPredicate
import io.github.dockyardmc.extentions.*
import io.github.dockyardmc.location.Location
import io.github.dockyardmc.player.ProfilePropertyMap
import io.github.dockyardmc.protocol.NetworkWritable
import io.github.dockyardmc.protocol.types.writeList
import io.github.dockyardmc.registry.AppliedPotionEffect
import io.github.dockyardmc.registry.Sounds
import io.github.dockyardmc.registry.registries.*
import io.github.dockyardmc.scroll.Component
import io.github.dockyardmc.scroll.CustomColor
import io.github.dockyardmc.sounds.Sound
import io.github.dockyardmc.world.World
import io.netty.buffer.ByteBuf
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTString
import java.util.*
import kotlin.reflect.KClass

object ItemComponents  {
    val components: MutableList<KClass<*>> = mutableListOf(
        CustomDataItemComponent::class,
        MaxStackSizeItemComponent::class,
        MaxDamageItemComponent::class,
        DamageItemComponent::class,
        UnbreakableItemComponent::class,
        CustomNameItemComponent::class,
        ItemNameItemComponent::class,
        ItemModelItemComponent::class,
        LoreItemComponent::class,
        RarityItemComponent::class,
        EnchantmentsItemComponent::class,
        CanBePlacedOnItemComponent::class,
        CanBreakItemComponent::class,
        AttributeModifiersItemComponent::class,
        CustomModelDataItemComponent::class,
        HideAdditionalTooltipItemComponent::class,
        HideTooltipItemComponent::class,
        RepairCostItemComponent::class,
        CreativeSlotLockItemComponent::class,
        EnchantmentGlintOverrideItemComponent::class,
        IntangibleProjectileItemComponent::class,
        FoodItemComponent::class,
        ConsumableItemComponent::class,
        UseRemainderItemComponent::class,
        UseCooldownItemComponent::class,
        DamageResistantItemComponent::class,
        ToolItemComponent::class,
        EnchantableItemComponent::class,
        EquippableItemComponent::class,
        RepairableItemComponent::class,
        GliderItemComponent::class,
        TooltipStyleItemComponent::class,
        DeathProtectionItemComponent::class,
        StoredEnchantments::class,
        DyedColorItemComponent::class,
        MapColorItemComponent::class,
        MapIdItemComponent::class,
        MapDecorationsItemComponent::class,
        MapPostProcessingItemComponent::class,
        ChargedProjectilesItemComponent::class,
        BundleContentsItemComponent::class,
        PotionContentsItemComponent::class,
        SuspiciousStewEffectsItemComponent::class,
        WritableBookContentItemComponent::class,
        WrittenBookContentItemComponent::class,
        TrimItemComponent::class,
        DebugStickItemComponent::class,
        EntityDataItemComponent::class,
        BucketEntityDataItemComponent::class,
        BlockEntityDataItemComponent::class,
        NoteBlockInstrumentItemComponent::class,
        OminousBottleAmplifierItemComponent::class,
        JukeboxPlayableItemComponent::class,
        RecipesItemComponent::class,
        LodestoneTrackerItemComponent::class,
        FireworkExplosionItemComponent::class,
        FireworksItemComponent::class,
        PlayerHeadProfileItemComponent::class,
        NoteBlockSoundItemComponent::class,
        BannerPatternsItemComponent::class,
        BaseColorItemComponent::class,
        PotDecorationsItemComponent::class,
        ContainerItemComponent::class,
        BlockStateItemComponent::class,
        BeesItemComponent::class,
        LockItemComponent::class,
        ContainerLootItemComponent::class
    )

}

interface ItemComponent

data class CustomDataItemComponent(
    var data: NBTCompound,
): ItemComponent

data class MaxStackSizeItemComponent(
    var maxStackSize: Int,
): ItemComponent

data class MaxDamageItemComponent(
    var maxDamage: Int,
): ItemComponent

data class DamageItemComponent(
    var damage: Int,
): ItemComponent

data class UnbreakableItemComponent(
    var showInTooltip: Boolean = false,
): ItemComponent

data class CustomNameItemComponent(
    var name: Component,
): ItemComponent

data class ItemNameItemComponent(
    var name: Component,
): ItemComponent

data class ItemModelItemComponent(
    val model: String
): ItemComponent

data class LoreItemComponent(
    var lines: Collection<Component>,
): ItemComponent

data class RarityItemComponent(
    var rarity: ItemRarity,
): ItemComponent

//TODO Enchantments
class EnchantmentsItemComponent(): ItemComponent

data class CanBePlacedOnItemComponent(
    var blocks: Collection<BlockPredicate>,
    var showInTooltip: Boolean = true,
): ItemComponent

data class CanBreakItemComponent(
    var blocks: Collection<BlockPredicate>,
    var showInTooltip: Boolean = true,
): ItemComponent

//TODO Attributes
data class AttributeModifiersItemComponent(
    val attributes: Collection<Modifier>,
    val showInTooltip: Boolean,
): ItemComponent

data class CustomModelDataItemComponent(
    val floats: List<Float> = listOf(),
    val flags: List<Boolean> = listOf(),
    val strings: List<String> = listOf(),
    val colors: List<Int> = listOf(),
): ItemComponent, NetworkWritable {

    override fun write(buffer: ByteBuf) {
        buffer.writeList<Float>(floats, ByteBuf::writeFloat)
        buffer.writeList<Boolean>(flags, ByteBuf::writeBoolean)
        buffer.writeList<String>(strings, ByteBuf::writeString)
        buffer.writeList<Int>(colors, ByteBuf::writeInt)
    }

    companion object {
        fun read(buffer: ByteBuf): CustomModelDataItemComponent {
            return CustomModelDataItemComponent(
                buffer.readList(ByteBuf::readFloat),
                buffer.readList(ByteBuf::readBoolean),
                buffer.readList(ByteBuf::readString),
                buffer.readList(ByteBuf::readInt),
            )
        }
    }
}


//what is this? even wiki.vg doesnt know
class HideAdditionalTooltipItemComponent(): ItemComponent

class HideTooltipItemComponent(): ItemComponent

data class RepairCostItemComponent(var repairCost: Int): ItemComponent

class CreativeSlotLockItemComponent(): ItemComponent

data class EnchantmentGlintOverrideItemComponent(var hasGlint: Boolean): ItemComponent

//note: write empty nbt compound as data
class IntangibleProjectileItemComponent(): ItemComponent

//TODO Potion effects
data class FoodItemComponent(
    var nutrition: Int,
    var saturation: Float = 0f,
    var canAlwaysEat: Boolean = true,
): ItemComponent

data class ConsumableItemComponent(
    val consumeSeconds: Float = 1.6f,
    val animation: ConsumableAnimation = ConsumableAnimation.EAT,
    val sound: Sound = Sound(Sounds.ENTITY_GENERIC_EAT),
    val hasConsumeParticles: Boolean,
    val consumeEffects: List<ConsumeEffect>
): ItemComponent {
    override fun toString(): String {
        return "ConsumableItemComponent(consumeSeconds=$consumeSeconds, animation=${animation.name}, sound=${sound.identifier}, hasConsumeParticles=$hasConsumeParticles)"
    }
}

data class UseRemainderItemComponent(
    val itemStack: ItemStack
): ItemComponent

data class UseCooldownItemComponent(
    val cooldownSeconds: Float,
    val cooldownGroup: String = "minecraft:all"
): ItemComponent

class DamageResistantItemComponent(
    val type: DamageType
): ItemComponent

data class ToolItemComponent(
    var toolRules: Collection<ToolRule>,
    var defaultMiningSpeed: Float,
    var damagePerBlock: Int,
): ItemComponent

//TODO Enchantments
data class EnchantableItemComponent(var value: Int): ItemComponent

data class EquippableItemComponent(
    val slot: EquipmentSlot,
    val equipSound: Sound,
    val assetId: String?,
    val cameraOverlay: String?,
    val allowedEntities: List<EntityType>,
    val dispensable: Boolean,
    val swappable: Boolean,
    val damageOnHurt: Boolean
): ItemComponent

data class RepairableItemComponent(
    val materials: List<Item>
): ItemComponent

class GliderItemComponent(): ItemComponent

data class TooltipStyleItemComponent(val texture: String): ItemComponent

data class DeathProtectionItemComponent(val effects: List<ConsumeEffect>): ItemComponent

//TODO
data class StoredEnchantments(val enchantments: List<String>, var showInTooltip: Boolean = true): ItemComponent

data class DyedColorItemComponent(var color: CustomColor, var showInTooltip: Boolean = false): ItemComponent

data class MapColorItemComponent(var color: CustomColor): ItemComponent

data class MapIdItemComponent(var mapId: Int): ItemComponent

data class MapDecorationsItemComponent(var nbt: NBTCompound): ItemComponent

data class MapPostProcessingItemComponent(var type: MapPostProcessing): ItemComponent

data class ChargedProjectilesItemComponent(var projectiles: Collection<ItemStack>): ItemComponent

data class BundleContentsItemComponent(var items: Collection<ItemStack>): ItemComponent

data class PotionContentsItemComponent(
    var potion: PotionEffect?,
    var customColor: CustomColor?,
    var potionEffects: Collection<AppliedPotionEffect> = listOf(),
    val customName: String?
): ItemComponent

data class SuspiciousStewEffectsItemComponent(var potionEffects: Collection<AppliedPotionEffect>): ItemComponent

data class WritableBookContentItemComponent(var pages: Collection<BookPage>): ItemComponent

data class WrittenBookContentItemComponent(
    var title: String,
    var filteredTitle: String?,
    var author: String,
    var generation: Int,
    var pages: Collection<BookPage>,
): ItemComponent

data class TrimItemComponent(
    val material: TrimMaterial,
    val pattern: TrimPattern,
    val showInTooltip: Boolean
): ItemComponent

data class DebugStickItemComponent(var data: NBTCompound): ItemComponent

data class EntityDataItemComponent(var data: NBTCompound): ItemComponent

data class BucketEntityDataItemComponent(var data: NBTCompound): ItemComponent

data class BlockEntityDataItemComponent(var data: NBTCompound): ItemComponent

data class NoteBlockInstrumentItemComponent(
    var instrument: String,
): ItemComponent

data class OminousBottleAmplifierItemComponent(var amplifier: Int): ItemComponent

data class JukeboxPlayableItemComponent(
    var directMode: Boolean,
    var sound: String?,
    var description: Component?,
    var duration: Float?,
    var output: Int? = 15,
    var showInTooltip: Boolean,
): ItemComponent

data class RecipesItemComponent(var recipes: List<String>): ItemComponent

data class LodestoneTrackerItemComponent(
    var hasGlobalPosition: Boolean,
    var dimension: World,
    var position: Location,
    var tracked: Boolean,
): ItemComponent

data class FireworkExplosionItemComponent(
    val shape: FireworkShape,
    val colors: Collection<CustomColor>,
    val fadeColors: Collection<CustomColor>,
    val hasTrail: Boolean,
    val hasTwinkle: Boolean
): ItemComponent

data class FireworksItemComponent(
    val flightDuration: Byte,
    val explosions: Collection<FireworkExplosionItemComponent>
): ItemComponent

data class PlayerHeadProfileItemComponent(
    var name: String?,
    var uuid: UUID?,
    var propertyMap: ProfilePropertyMap,
): ItemComponent

data class NoteBlockSoundItemComponent(val sound: String): ItemComponent

data class BannerPatternsItemComponent(val layers: Collection<BannerPatternLayer>): ItemComponent

data class BaseColorItemComponent(val color: DyeColor): ItemComponent

//TODO pot decoration registry
class PotDecorationsItemComponent(
//    var decorations: Collection<>,
): ItemComponent

//data class BannerShieldBaseColorItemComponent(var color: LegacyTextColor): ItemComponent

data class ContainerItemComponent(var items: Collection<ItemStack>): ItemComponent

data class ContainerItemStack(
    val slot: Int,
    val itemStack: ItemStack
)

fun ByteBuf.readContainerItemStack(): ContainerItemStack {
    return ContainerItemStack(
        this.readVarInt(),
        ItemStack.read(this)
    )
}

fun ByteBuf.readContainerItemStackList(): Collection<ContainerItemStack> {
    val list = mutableListOf<ContainerItemStack>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readContainerItemStack())
    }
    return list
}

fun ByteBuf.writeContainerItemStackList(list: Collection<ContainerItemStack>) {
    this.writeVarInt(list.size)
    list.forEach {
        this.writeContainerItemStack(it)
    }
}

fun ByteBuf.writeContainerItemStack(containerItemStack: ContainerItemStack) {
    this.writeVarInt(containerItemStack.slot)
    containerItemStack.itemStack.write(this)
}

data class BlockStateItemComponent(val states: Map<String, String>): ItemComponent

data class BeesItemComponent(var bees: Collection<BeeInsideBeehive>): ItemComponent

// TODO(1.21.2) Updated NBT format > https://minecraft.wiki/w/Java_Edition_1.21.2#Data_components_3
data class LockItemComponent(var key: NBTString): ItemComponent

data class ContainerLootItemComponent(var loot: NBTCompound): ItemComponent

data class BeeInsideBeehive(
    var entityData: NBTCompound,
    var ticksInHive: Int,
    var minTicksInHive: Int
)

data class BannerPatternLayer(
    var pattern: BannerPattern,
    var color: DyeColor
)

fun ByteBuf.readBannerPatternLayer(): BannerPatternLayer {
    val bannerPattern: BannerPattern
    val type = this.readVarInt() - 1

    bannerPattern = if(type != -1) BannerPatternRegistry.getByProtocolId(type + 1) else BannerPatternRegistry[this.readString()]
    val dyeColor = this.readVarIntEnum<DyeColor>()

    return BannerPatternLayer(
        bannerPattern,
        dyeColor
    )
}

fun ByteBuf.writeBannerPatternLayer(bannerPattern: BannerPattern, dyeColor: DyeColor) {
    if(bannerPattern.identifier.contains("minecraft:")) {
        this.writeVarInt(bannerPattern.getProtocolId())
    } else {
        this.writeString(bannerPattern.identifier)
    }
    this.writeVarIntEnum<DyeColor>(dyeColor)
}

fun ByteBuf.writeBannerPatternLayerList(list: Collection<BannerPatternLayer>) {
    this.writeVarInt(list.size)
    list.forEach { this.writeBannerPatternLayer(it.pattern, it.color) }
}

fun ByteBuf.readBannerPatternLayerList(): List<BannerPatternLayer> {
    val list = mutableListOf<BannerPatternLayer>()
    for (i in 0 until this.readVarInt()) {
        list.add(this.readBannerPatternLayer())
    }
    return list
}


data class BookPage(
    var rawContent: String,
    var filteredContent: String? = null
)

fun ByteBuf.writeBookPages(pages: Collection<BookPage>) {
    this.writeVarInt(pages.size)
    pages.forEach {
        this.writeString(it.rawContent)
        this.writeOptionalOLD(it.filteredContent) { op ->
            op.writeString(it.filteredContent!!)
        }
    }
}

fun ByteBuf.readBookPages(): List<BookPage> {
    val pages = mutableListOf<BookPage>()
    val size = this.readVarInt()
    for (i in 0 until size) {
        val content = this.readString()
        val filteredContent = if(this.readBoolean()) this.readString() else null
        pages.add(BookPage(content, filteredContent))
    }
    return pages
}

fun MutableList<ItemComponent>.addOrUpdate(newComponent: ItemComponent) {
    if(this.firstOrNull { it::class == newComponent::class } != null) {
        val index = this.indexOfFirst { it::class == newComponent::class }
        this[index] = newComponent
    } else {
        this.add(newComponent)
    }
}

fun MutableList<ItemComponent>.removeByType(type: KClass<*>) {
    this.forEach { if (it::class == type) this.remove(it) }
}

fun Collection<ItemComponent>.hasType(type: KClass<*>): Boolean =
    this.firstOrNull { it::class == type } != null

@Suppress("UNCHECKED_CAST")
fun <T : ItemComponent> Collection<ItemComponent>.getOrNull(type: KClass<T>): T? {
    val component =  this.firstOrNull { it::class == type }
    return if(component == null) null else component as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Collection<ItemComponent>.firstOrNullByType(type: KClass<*>): T? {
    val value = this.firstOrNull { it::class == type } ?: return null
    return value as T
}


data class ToolRule(
    val blocks: Collection<io.github.dockyardmc.world.block.Block>,
    val speed: Float?,
    val correctDropForBlocks: Boolean
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

enum class ConsumableAnimation {
    NONE,
    EAT,
    DRINK,
    BLOCK,
    BOW,
    SPEAR,
    CROSSBOW,
    SPYGLASS,
    TOOT_HORN,
    BRUSH,
    BUNDLE,
}

interface ConsumeEffect

class ApplyEffectsConsumeEffect(
    val effects: List<AppliedPotionEffect>,
    val probability: Float = 1f
): ConsumeEffect

class RemoveEffectsConsumeEffect(
    val effects: List<PotionEffect>,
): ConsumeEffect

class ClearAllEffectsConsumeEffect: ConsumeEffect

class TeleportRandomlyConsumeEffect(
    val diameter: Float = 16.0f,
): ConsumeEffect

class PlaySoundConsumeEffect(
    val sound: Sound
): ConsumeEffect

enum class EquipmentSlot {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    BODY,
    MAIN_HAND,
    OFF_HAND;

    companion object {
        fun isBody(equipmentSlot: EquipmentSlot?): Boolean {
            if(equipmentSlot == null) return false
            if(equipmentSlot == MAIN_HAND) return false
            if(equipmentSlot == OFF_HAND) return false
            return true
        }
    }
}

enum class FireworkShape {
    SMALL_BALL,
    LARGE_BALL,
    STAR,
    CREEPER,
    BURST
}

enum class DyeColor {
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHT_BLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHT_GRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK
}