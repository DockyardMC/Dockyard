package io.github.dockyardmc.data

import io.github.dockyardmc.data.components.*
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object DataComponentRegistry {

    val protocolIdCounter = AtomicInteger()
    val dataComponentsById = Int2ObjectOpenHashMap<KClass<out DataComponent>>()
    val dataComponentsByIdentifier = Object2ObjectOpenHashMap<String, KClass<out DataComponent>>()

    val dataComponentsByIdReversed = Object2IntOpenHashMap<KClass<out DataComponent>>()
    val dataComponentsByIdentifierReversed = Object2ObjectOpenHashMap<KClass<out DataComponent>, String>()

    val CUSTOM_DATA = register("minecraft:custom_data", CustomDataComponent::class)
    val MAX_STACK_SIZE = register("minecraft:max_stack_size", MaxStackSizeComponent::class)
    val MAX_DAMAGE = register("minecraft:max_damage", MaxDamageComponent::class)
    val DAMAGE = register("minecraft:damage", DamageComponent::class)
    val UNBREAKABLE = register("minecraft:unbreakable", UnbreakableComponent::class)
    val CUSTOM_NAME = register("minecraft:custom_name", CustomNameComponent::class)
    val ITEM_NAME = register("minecraft:item_name", ItemNameComponent::class)
    val ITEM_MODEL = register("minecraft:item_model", ItemModelComponent::class)
    val LORE = register("minecraft:lore", LoreComponent::class)
    val RARITY = register("minecraft:rarity", RarityComponent::class)
    val ENCHANTMENTS = register("minecraft:enchantments", EnchantmentsComponent::class)
    val CAN_PLACE_ON = register("minecraft:can_place_on", CanPlaceOnComponent::class)
    val CAN_BREAK = register("minecraft:can_break", CanBreakComponent::class)
    val ATTRIBUTE_MODIFIERS = register("minecraft:attribute_modifiers", AttributeModifiersComponent::class)
    val CUSTOM_MODEL_DATA = register("minecraft:custom_model_data", CustomModelDataComponent::class)
    val TOOLTIP_DISPLAY = register("minecraft:tooltip_display", TooltipDisplayComponent::class)
    val REPAIR_COST = register("minecraft:repair_cost", RepairCostComponent::class)
    val CREATIVE_SLOT_LOCK = register("minecraft:creative_slot_lock", CreativeSlotLockComponent::class)
    val ENCHANTMENT_GLINT_OVERRIDE = register("minecraft:enchantment_glint_override", EnchantmentGlintOverrideComponent::class)
    val FOOD = register("minecraft:food", FoodComponent::class)
    val CONSUMABLE = register("minecraft:consumable", ConsumableComponent::class)
    val USE_REMAINDER = register("minecraft:use_remainder", UseRemainderComponent::class)
    val USE_COOLDOWN = register("minecraft:use_cooldown", CooldownItemComponent::class)
    val DAMAGE_RESISTANT = register("minecraft:damage_resistant", DamageResistantComponent::class)
    val TOOL = register("minecraft:tool", ToolComponent::class)
    val WEAPON = register("minecraft:weapon", WeaponComponent::class)
    val EQUIPABBLE = register("minecraft:equippable", EquippableComponent::class)
    val REPAIRABLE = register("minecraft:repairable", RepairableComponent::class)
    val GLIDER = register("minecraft:glider", GliderComponent::class)
    val TOOLTIP_STYLE = register("minecraft:tooltip_style", TooltipStyleComponent::class)

    fun register(identifier: String, kclass: KClass<out DataComponent>): KClass<out DataComponent> {
        val protocolId = protocolIdCounter.getAndIncrement()
        dataComponentsById[protocolId] = kclass
        dataComponentsByIdReversed[kclass] = protocolId

        dataComponentsByIdentifier[identifier] = kclass
        dataComponentsByIdentifierReversed[kclass] = identifier

        return kclass
    }
}