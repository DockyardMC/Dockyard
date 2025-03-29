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

    fun register(identifier: String, kclass: KClass<out DataComponent>): KClass<out DataComponent> {
        val protocolId = protocolIdCounter.getAndIncrement()
        dataComponentsById[protocolId] = kclass
        dataComponentsByIdReversed[kclass] = protocolId

        dataComponentsByIdentifier[identifier] = kclass
        dataComponentsByIdentifierReversed[kclass] = identifier

        return kclass
    }
}