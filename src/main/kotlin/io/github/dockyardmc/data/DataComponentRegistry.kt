package io.github.dockyardmc.data

import io.github.dockyardmc.data.components.CustomDataComponent
import io.github.dockyardmc.data.components.MaxDamageComponent
import io.github.dockyardmc.data.components.MaxStackSizeComponent
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

object DataComponentRegistry {

    private val protocolIdCounter = AtomicInteger()
    private val dataComponentsById = Int2ObjectOpenHashMap<KClass<out DataComponent>>()
    private val dataComponentsByIdentifier = Object2ObjectOpenHashMap<String, KClass<out DataComponent>>()

    private val dataComponentsByIdReversed = Object2IntOpenHashMap<KClass<out DataComponent>>()
    private val dataComponentsByIdentifierReversed = Object2ObjectOpenHashMap<KClass<out DataComponent>, String>()

    val CUSTOM_DATA = register("minecraft:custom_data", CustomDataComponent::class)
    val MAX_STACK_SIZE = register("minecraft:max_stack_size", MaxStackSizeComponent::class)
    val MAX_DAMAGE = register("minecraft:max_damage", MaxDamageComponent::class)

    fun register(identifier: String, kclass: KClass<out DataComponent>): KClass<out DataComponent> {
        val protocolId = protocolIdCounter.getAndIncrement()
        dataComponentsById[protocolId] = kclass
        dataComponentsByIdReversed[kclass] = protocolId

        dataComponentsByIdentifier[identifier] = kclass
        dataComponentsByIdentifierReversed[kclass] = identifier

        return kclass
    }
}